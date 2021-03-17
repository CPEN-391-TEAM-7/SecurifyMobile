package com.example.securify.bluetooth;

import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothStreams {
    private static BluetoothStreams BLUETOOTH_STREAMS = null;
    private InputStream InputStream;
    private OutputStream OutputStream;

    public static BluetoothStreams getInstance()
    {
        if (BLUETOOTH_STREAMS == null) {
            BLUETOOTH_STREAMS = new BluetoothStreams();
        }
        return BLUETOOTH_STREAMS;
    }

    private BluetoothStreams() {}

    public InputStream getInputStream()  {return InputStream;}
    public OutputStream getOutputStream()  {return OutputStream;}

    public void setInputStream(InputStream inputStream) {this.InputStream = inputStream;}
    public void setOutputStream(OutputStream outputStream) {this.OutputStream = outputStream;}



}
