package com.geonet.blauzahn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.UUID;


/**
 * Created by SE on 14.03.2018.
 */


public class ChatFragment extends Fragment {

    public static final String TAG = "CHAT";
    private BluetoothDevice targetDevice;
    private TextView textView;
    private BluetoothAdapter bluetoothAdapter;


    public ChatFragment() {
    }

    @NonNull
    public static ChatFragment createChatFragment(@NonNull BluetoothDevice targetDevice, @NonNull BluetoothAdapter bluetoothAdapter) {

        ChatFragment fragment = new ChatFragment();
        fragment.targetDevice = targetDevice;
        fragment.bluetoothAdapter = bluetoothAdapter;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        textView = rootView.findViewById(R.id.textViewChat);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean bond = targetDevice.createBond();



    }
}