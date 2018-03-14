package com.geonet.blauzahn;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothDeviceDecorator;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothService bluetoothService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothConfiguration config = new BluetoothConfiguration();
        config.context = this;
        config.bluetoothServiceClass = BluetoothClassicService.class; // BluetoothClassicService.class or BluetoothLeService.class
        config.bufferSize = 1024;
        config.characterDelimiter = '\n';
        config.deviceName = "Your App Name";
        config.callListenersInMainThread = true;
        config.uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Set null to find all devices on scan.

        BluetoothService.init(config);
        bluetoothService = BluetoothService.getDefaultInstance();

        DeviceListFragment deviceListFragment = new DeviceListFragment();
        deviceListFragment.setOnDeviceSelectedListener(device -> {
            getSupportFragmentManager().beginTransaction().add(R.id.rootFrame, ChatFragment.createChatFragment(device), ChatFragment.TAG).commit();
        });


        // Set initial DeviceListFragment
        getSupportFragmentManager().beginTransaction().add(R.id.rootFrame, deviceListFragment, DeviceListFragment.TAG).commit();

        bluetoothService.setOnEventCallback(new BluetoothService.OnBluetoothEventCallback() {
            @Override
            public void onDataRead(byte[] bytes, int i) {

            }

            @Override
            public void onStatusChange(BluetoothStatus bluetoothStatus) {
                Log.d("@BT_MESSENGER", "onStatusChange: " + bluetoothStatus.toString());
            }

            @Override
            public void onDeviceName(String s) {
                Log.d("@BT_MESSENGER", "onDeviceName: " + s);
            }

            @Override
            public void onToast(String s) {

            }

            @Override
            public void onDataWrite(byte[] bytes) {

            }
        });


    }


}
