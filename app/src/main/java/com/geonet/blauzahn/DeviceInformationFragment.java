package com.geonet.blauzahn;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_DUAL;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_UNKNOWN;


/**
 * Created by SE on 14.03.2018.
 */
public class DeviceInformationFragment extends Fragment {

    public static final String TAG = "INFO";
    private BluetoothDevice targetDevice;
    private TextView textName;
    private TextView textMAC;
    private TextView textType;
    private TextView textUUIDs;


    // Required public empty CTor.
    public DeviceInformationFragment() {
    }

     /**
     * @param targetDevice
     * @return
     */
    @NonNull
    public static DeviceInformationFragment createInformationFragment(@NonNull BluetoothDevice targetDevice) {

        DeviceInformationFragment fragment = new DeviceInformationFragment();
        fragment.targetDevice = targetDevice;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        textName = rootView.findViewById(R.id.textName);
        textMAC = rootView.findViewById(R.id.textMac);
        textType = rootView.findViewById(R.id.textType);
        textUUIDs = rootView.findViewById(R.id.textUUIDs);

        Button buttonConnect = rootView.findViewById(R.id.buttonConnect);
        buttonConnect.setOnClickListener(v -> {

            // The following code is blocking, make sure to handle is asynchronously!
            Thread thread = new Thread(() -> {

                // If a connection cannot be established, an exception will be thrown!
                try {

                    // Create a new socket for the target device
                    final BluetoothSocket bluetoothSocket = targetDevice.createRfcommSocketToServiceRecord(UUID.randomUUID());
                    bluetoothSocket.connect();

                    // If this part is reached, the connection was successful
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> Toast.makeText(getContext(), "Verbindung zum Gerät erfolgreich hergestellt!", Toast.LENGTH_SHORT).show());

                } catch (Exception e) {

                    // The connection failed.
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> Toast.makeText(getContext(), "Verbindung konnte nicht hergestellt werden!\n" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                }
            });
            thread.start();


        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Display all information about the device

        textName.setText(targetDevice.getName());
        textMAC.setText(targetDevice.getAddress());

        final int type = targetDevice.getType();
        switch (type) {
            case DEVICE_TYPE_CLASSIC:
                textType.setText("Classic");
                break;

            case DEVICE_TYPE_DUAL:
                textType.setText("Dual");
                break;

            case DEVICE_TYPE_LE:
                textType.setText("LE");
                break;

            case DEVICE_TYPE_UNKNOWN:
                textType.setText("Unknown");
                break;
        }


        StringBuilder stringBuilder = new StringBuilder();
        for (ParcelUuid parcelUuid : targetDevice.getUuids()) {
            stringBuilder.append(parcelUuid.getUuid().toString());
            stringBuilder.append("\n");
        }
        textUUIDs.setText(stringBuilder.toString());

    }
}