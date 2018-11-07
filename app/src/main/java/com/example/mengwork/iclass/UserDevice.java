package com.example.mengwork.iclass;

import android.bluetooth.BluetoothDevice;

public class UserDevice {

    private String userInfo;
    private String bluetoothAddress;

    public UserDevice(String bluetoothName, String bluetoothAddress){
        userInfo = bluetoothName;
        this.bluetoothAddress = bluetoothAddress;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public String getBluetoothAddress() {
        return bluetoothAddress;
    }
}
