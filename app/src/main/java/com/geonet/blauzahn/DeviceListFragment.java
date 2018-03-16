package com.geonet.blauzahn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by SE on 14.03.2018.
 */


public class DeviceListFragment extends Fragment {

    public static final String TAG = "DEVICE_LIST";
    private DevicesAdapter devicesAdapter = new DevicesAdapter();
    private TextView textFoundDevices;

    // Create a BroadcastReceiver for ACTION_FOUND & ACTION_BOND_STATE_CHANGED
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            // New device was found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devicesAdapter.addDevice(device);

                Log.d(TAG, "onReceive: Device found");
            }

            // Device bonding changed
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);

                // Select device after bonding
                if (bondState == BluetoothDevice.BOND_BONDED && device != null) {
                    onDeviceSelectedListener.deviceSelected(device);
                }

            }

        }

    };
    private OnDeviceSelectedListener onDeviceSelectedListener;
    private BluetoothAdapter bluetoothAdapter;

    // Required empty public CTor
    public DeviceListFragment() {
    }

    /**
     * Creates a new instance of {@link DeviceListFragment}.
     *
     * @param bluetoothAdapter {@link BluetoothAdapter} An active bluetooth adapter.
     * @return The new instance of {@link DeviceListFragment}.
     */
    @NonNull
    public static DeviceListFragment createDeviceListFragment(@NonNull BluetoothAdapter bluetoothAdapter) {

        DeviceListFragment fragment = new DeviceListFragment();
        fragment.bluetoothAdapter = bluetoothAdapter;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Register for broadcasts when a device is discovered.
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, discoveryFilter);

        // Register for broadcasts when the bonding state changes.
        IntentFilter bondingFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(broadcastReceiver, bondingFilter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Basic interface inflation
        View rootView = inflater.inflate(R.layout.fragment_list_devices, container, false);

        textFoundDevices = rootView.findViewById(R.id.textFoundDevices);
        Button buttonScan = rootView.findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(view -> {
            scanForDevices();
            textFoundDevices.setText("Suche l\u00e4uft...");
        });

        // Setup the RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.deviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(devicesAdapter);

        // Inflate me
        return rootView;
    }

    // Close the broadcast receiver when the fragment is detached from the activity.
    @Override
    public void onDetach() {
        super.onDetach();

        // Remove receiver
        Objects.requireNonNull(getActivity()).unregisterReceiver(broadcastReceiver);
    }


    /**
     * Starts the device discovery.
     */
    private void scanForDevices() {

        // Remove all entries
        devicesAdapter.clearList();

        // Start device discovery
        bluetoothAdapter.startDiscovery();

    }

    /**
     * Tries to connect to a device.
     *
     * @param device {@link BluetoothAdapter} The device to connect to.
     */
    void tryConnectToDevice(final BluetoothDevice device) {

        // If device is already bonded, call listener immediately
        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            onDeviceSelectedListener.deviceSelected(device);
            return;
        }

        // If not, create a bond and wait for the BOND_STATE_CHANGED broadcast.
        Toast.makeText(getContext(), "Warte auf Ger\u00e4t...", Toast.LENGTH_LONG).show();
        device.createBond();

    }

    /**
     * Sets a listener to handle a device selection.
     *
     * @param listener {@link OnDeviceSelectedListener}
     */
    void setOnDeviceSelectedListener(OnDeviceSelectedListener listener) {
        onDeviceSelectedListener = listener;
    }


    interface OnDeviceSelectedListener {

        /**
         * Is called when the user wants to connect to a device.
         *
         * @param device {@link BluetoothDevice}
         */
        void deviceSelected(BluetoothDevice device);
    }


    /**
     * Private adapter class for the RecyclerView
     */
    private class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

        // The dataset
        private List<BluetoothDevice> bluetoothDeviceList = new LinkedList<>();

        /**
         * Adds a new device to the RecyclerView.
         *
         * @param device {@link BluetoothDevice} The Device to add.
         */
        void addDevice(BluetoothDevice device) {

            // Make sure not to add duplicates
            if (!bluetoothDeviceList.contains(device)) {
                bluetoothDeviceList.add(device);

                // Required for RecyclerView
                notifyDataSetChanged();

                textFoundDevices.setText(String.format("Geräte gefunden: %d", getItemCount()));
            }
        }


        /**
         * Clears the RecyclerView of all devices.
         */
        void clearList() {
            bluetoothDeviceList.clear();
            notifyDataSetChanged();
        }


        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            // create a new view
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_element, parent, false);
            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            final String name = bluetoothDeviceList.get(position).getName();

            if (name != null && !name.isEmpty()) {
                holder.getTextName().setText(name);
            } else {
                holder.getTextName().setText("n/a");
            }

            holder.getRootView().setOnClickListener(view -> {

                if (onDeviceSelectedListener != null) {
                    tryConnectToDevice(bluetoothDeviceList.get(position));
                }

            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return bluetoothDeviceList.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {

            private final View rootView;
            private final TextView textName;

            // Creates a new view for the RecyclerView
            ViewHolder(View rootView) {
                super(rootView);

                this.rootView = rootView;
                textName = rootView.findViewById(R.id.recyclerItemName);
            }

            View getRootView() {
                return rootView;
            }

            TextView getTextName() {
                return textName;
            }
        }
    }

}
