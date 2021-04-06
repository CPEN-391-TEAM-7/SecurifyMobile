package com.example.securify.bluetooth;

import java.io.OutputStream;

/**
 * Singleton class that holds Bluetooth OutputStream
 */
public class BluetoothOutputStream {
    private static BluetoothOutputStream BLUETOOTH_STREAMS = null;
    private OutputStream OutputStream;

    public static BluetoothOutputStream getInstance()
    {
        if (BLUETOOTH_STREAMS == null) {
            BLUETOOTH_STREAMS = new BluetoothOutputStream();
        }
        return BLUETOOTH_STREAMS;
    }

    private BluetoothOutputStream() {}

    public OutputStream getOutputStream()  {return OutputStream;}

    public void setOutputStream(OutputStream outputStream) {this.OutputStream = outputStream;}


}
