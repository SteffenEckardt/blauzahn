package com.geonet.blauzahn;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothDeviceDecorator;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * Created by SE on 14.03.2018.
 */
public class DeviceListFragment extends Fragment implements BluetoothService.OnBluetoothScanCallback {

    public static final String TAG = "DEVICE_LIST";
    private DevicesAdapter devicesAdapter;
    private TextView textFoundDevices;
    private BluetoothService bluetoothService;

    private OnDeviceSelectedListener onDeviceSelectedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_list_devices, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.deviceList);

        textFoundDevices = rootView.findViewById(R.id.textFoundDevices);

        Button buttonScan = rootView.findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(view -> {
            if (bluetoothService != null) {
                bluetoothService.startScan();
                Toast.makeText(getContext(), "Scanne nach Geräten", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Bluetooth Service nicht verfügbar", Toast.LENGTH_SHORT).show();
            }
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
    public void onStart() {
        super.onStart();

        if (bluetoothService == null) {
            bluetoothService = BluetoothService.getDefaultInstance();
        }

        bluetoothService.setOnScanCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (bluetoothService != null) {
            bluetoothService.stopService();
        }

    }

    void setOnDeviceSelectedListener(OnDeviceSelectedListener listener) {
        onDeviceSelectedListener = listener;
    }

    @Override
    public void onDeviceDiscovered(BluetoothDevice bluetoothDevice, int i) {
        bluetoothService.connect(bluetoothDevice);
    }

    @Override
    public void onStartScan() {

    }

    @Override
    public void onStopScan() {

    }

    interface OnDeviceSelectedListener {
        void deviceSelected(BluetoothDevice device);
    }


    private class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

        private List<BluetoothDevice> bluetoothDeviceList = new LinkedList<>();

        void addDevice(BluetoothDevice device) {

            bluetoothDeviceList.add(device);
            notifyDataSetChanged();

            textFoundDevices.setText(String.format("Found Devices: %d", getItemCount()));

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
                    onDeviceSelectedListener.deviceSelected(bluetoothDeviceList.get(position));
                }

            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return bluetoothDeviceList.size();
        }

        /**
         * TODO
         */
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
