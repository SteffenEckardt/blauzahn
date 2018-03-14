package com.geonet.blauzahn;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothClassicService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothConfiguration;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothService;
import com.github.douglasjunior.bluetoothclassiclibrary.BluetoothStatus;

import java.util.UUID;

/**
 * Created by SE on 14.03.2018.
 */
public class ChatFragment extends Fragment implements BluetoothService.OnBluetoothEventCallback {

    public static final String TAG = "CHAT";
    private BluetoothDevice targetDevice;
    private TextView textView;

    public ChatFragment() {
    }

    @NonNull
    public static ChatFragment createChatFragment(@NonNull BluetoothDevice targetDevice) {

        ChatFragment fragment = new ChatFragment();
        fragment.targetDevice = targetDevice;

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

        BluetoothService bluetoothService = BluetoothService.getDefaultInstance();
        bluetoothService.setOnEventCallback(this);
    }

    @Override
    public void onDataRead(byte[] bytes, int i) {

    }

    @Override
    public void onStatusChange(BluetoothStatus bluetoothStatus) {
        textView.setText(bluetoothStatus.toString());
    }

    @Override
    public void onDeviceName(String s) {
        textView.setText(s);
    }

    @Override
    public void onToast(String s) {

    }

    @Override
    public void onDataWrite(byte[] bytes) {

    }
}
