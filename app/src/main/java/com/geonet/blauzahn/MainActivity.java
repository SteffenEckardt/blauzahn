/*
 * Copyright (c) 2018. Steffen Eckardt, Andreas Klar, Yves Weilandt @ TH-Bingen
 */

package com.geonet.blauzahn;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/*
 * Copyright:
 *
 * Steffen Eckardt
 * Andreas Klar
 * Yves Weilandt
 *
 * @TH Bingen
 *
 * 15.03.2018
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the default bluetooth adapter.
        // If none is available, goto: Hell!
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            return;
        }

        // Check if the adapter is enabled, if not, prompt user to do so!
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        // All checks were passed, start the action.
        startLogic();

    }


    /**
     * This starts the application logic.
     */
    void startLogic() {

        // Get a fixed DeviceListFragment.
        DeviceListFragment deviceListFragment = DeviceListFragment.createDeviceListFragment(bluetoothAdapter);

        // Handle what happens, if the user selects a device.
        deviceListFragment.setOnDeviceSelectedListener(device -> getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rootFrame, DeviceInformationFragment.createInformationFragment(device), DeviceInformationFragment.TAG)
                .addToBackStack(null)
                .commit());

        // Show the search fragment.
        getSupportFragmentManager().beginTransaction().add(R.id.rootFrame, deviceListFragment, DeviceListFragment.TAG).commit();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // User activated bluetooth, start logic.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            startLogic();
        }

    }
}
