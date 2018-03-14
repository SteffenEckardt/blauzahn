package com.geonet.blauzahn;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.primary_recycler);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new DevicesAdapter(new String[]{"1", "2", "3"});
        mRecyclerView.setAdapter(mAdapter);


        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = getApplicationContext();
        config.bluetoothServiceClass = BluetoothClassicService.class; // BluetoothClassicService.class or BluetoothLeService.class
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "Your App Name";
        config.callListenersInMainThread = true;

        // Bluetooth Classic
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Set null to find all devices on scan.

        BluetoothService.init(config);
        BluetoothService bluetoothService = BluetoothService.getDefaultInstance();

        bluetoothService.setOnScanCallback(new BluetoothService.OnBluetoothScanCallback() {
            @Override
            public void onDeviceDiscovered(BluetoothDevice bluetoothDevice, int i) {

            }

            @Override
            public void onStartScan() {

            }

            @Override
            public void onStopScan() {

            }
        });

        bluetoothService.startScan();

    }

    public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {

        private String[] mDeviceList;

        class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            TextView mTextViewAdress;

            ViewHolder(TextView adressView) {
                super(adressView);
                mTextViewAdress = adressView;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        DevicesAdapter(String[] myDataset) {
            mDeviceList = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public DevicesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // create a new view
            TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_element, parent, false);

            return new ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextViewAdress.setText(mDeviceList[position]);

        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDeviceList.length;
        }
    }

}
