package com.geonet.blauzahn;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
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


import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by SE on 14.03.2018.
 */


public class DeviceListFragment extends Fragment {

    public static final String TAG = "DEVICE_LIST";
    private DevicesAdapter devicesAdapter;
    private TextView textFoundDevices;


    private OnDeviceSelectedListener onDeviceSelectedListener;
    private BluetoothAdapter bluetoothAdapter;
    private Button buttonScan;

    public DeviceListFragment() {
    }

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

        IntentFilter bondingFilter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(broadcastReceiver, bondingFilter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_devices, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.deviceList);

        textFoundDevices = rootView.findViewById(R.id.textFoundDevices);

        buttonScan = rootView.findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(view -> {
            scanForDevices();
        });

        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // specify an adapter (see also next example)
        devicesAdapter = new DevicesAdapter();
        recyclerView.setAdapter(devicesAdapter);

        //Inflate me
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Remove receiver
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private void scanForDevices() {


        if (!bluetoothAdapter.isDiscovering()) {

            // Get already bonded devices
            for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
                devicesAdapter.addDevice(device);
            }

            // Remove all entries
            devicesAdapter.clearList();

            // Start device discovery
            bluetoothAdapter.startDiscovery();

            // Change button text
            buttonScan.setText("STOP");

        } else {

            // Stop discovering
            bluetoothAdapter.cancelDiscovery();

            // Change button text
            buttonScan.setText("SCAN");

        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
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
            }

            // Device bonding changed
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);

            }

        }

    };


    void tryConnectToDevice(final BluetoothDevice device) {

        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            device.createBond();
        }

        Thread thread = new Thread(() -> {

            try {

                final BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.randomUUID());

                if (bluetoothSocket != null && !bluetoothSocket.isConnected()) {
                    bluetoothAdapter.cancelDiscovery();
                    bluetoothSocket.connect();

                    // Notify user about new connection
                    Toast.makeText(getActivity(), "BONDED TO: " + bluetoothSocket.getRemoteDevice().getAddress(), Toast.LENGTH_SHORT).show();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        thread.start();

        //if () {
        //    onDeviceSelectedListener.deviceSelected(device);
        //}

    }

    void setOnDeviceSelectedListener(OnDeviceSelectedListener listener) {
        onDeviceSelectedListener = listener;
    }


    interface OnDeviceSelectedListener {
        void deviceSelected(BluetoothDevice device);
    }


    private class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

        private List<BluetoothDevice> bluetoothDeviceList = new LinkedList<>();

        void addDevice(BluetoothDevice device) {

            if(!bluetoothDeviceList.contains(device)) {
                bluetoothDeviceList.add(device);
                notifyDataSetChanged();

                textFoundDevices.setText(String.format("Found Devices: %d", getItemCount()));
            }
        }


        void clearList() {
            bluetoothDeviceList.clear();
            notifyDataSetChanged();
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public DevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_element, parent, false);

            return new DevicesAdapter.ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull DevicesAdapter.ViewHolder holder, int position) {
            holder.getmTextViewAddress().setText(bluetoothDeviceList.get(position).getName());

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

            public View getRootView() {
                return rootView;
            }

            public TextView getmTextViewAddress() {
                return mTextViewAddress;
            }

            private final View rootView;
            private TextView mTextViewAddress;

            ViewHolder(View rootView) {
                super(rootView);

                this.rootView = rootView;

                mTextViewAddress = ((TextView) rootView);
            }
        }
    }

}
