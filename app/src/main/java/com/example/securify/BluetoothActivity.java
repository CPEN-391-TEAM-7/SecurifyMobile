package com.example.securify;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.icu.number.LocalizedNumberFormatter;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.securify.ui.adapters.DeviceArrayAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class BluetoothActivity extends AppCompatActivity {

    // A constant that we use to determine if our request to turn on bluetooth worked
    private final static int REQUEST_ENABLE_BT = 1;
    // A handle to the tablet’s bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter;

    private Context context;

    private DeviceArrayAdapter PairedArrayAdapter;
    private DeviceArrayAdapter DiscoveredArrayAdapter;


    private final ArrayList<BluetoothDevice> DiscoveredDevices = new ArrayList<>();
    private final ArrayList<String> DiscoveredDevicesArray = new ArrayList<>();

    private final ArrayList<BluetoothDevice> PairedDevices = new ArrayList<>();
    private final ArrayList<String> PairedDevicesArray = new ArrayList<>();

    private BluetoothSocket mmSocket = null;
    public static InputStream mmInStream = null;
    public static OutputStream mmOutStream = null;

    private boolean isConnected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        context = getApplicationContext();

        PairedArrayAdapter = new DeviceArrayAdapter(this, R.layout.row, PairedDevicesArray);
        DiscoveredArrayAdapter = new DeviceArrayAdapter(this, R.layout.row, DiscoveredDevicesArray);

        ListView pairedListView = findViewById(R.id.pairedList);
        ListView discoveredListView = findViewById(R.id.discoveredList);

        pairedListView.setOnItemClickListener(mPairedClickedHandler);
        discoveredListView.setOnItemClickListener(mDiscoveredClickedHandler);

        pairedListView.setAdapter(PairedArrayAdapter);
        discoveredListView.setAdapter(DiscoveredArrayAdapter);

        // TODO: request location permissions

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "Device does not support bluetooth", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent =  new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        IntentFilter filterFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterStart = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filterStop = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filterFound);
        registerReceiver(mReceiver, filterStart);
        registerReceiver(mReceiver, filterStop);


        Set<BluetoothDevice> thePairedDevices = mBluetoothAdapter.getBondedDevices();

        if(thePairedDevices.size() > 0 ) {
            Iterator<BluetoothDevice> iter = thePairedDevices.iterator();
            BluetoothDevice aNewDevice;

            while(iter.hasNext()) {
                aNewDevice = iter.next();
                String PairedDevice = aNewDevice.getName() + "\nMAC Address\n" + aNewDevice.getAddress();

                PairedDevices.add(aNewDevice);
                PairedDevicesArray.add(PairedDevice);
                PairedArrayAdapter.notifyDataSetChanged();
            }
        }

        if(mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();

        mBluetoothAdapter.startDiscovery();


    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice newDevice;

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!DiscoveredDevices.contains(newDevice)) {
                    String device = newDevice.getName() + "\nMAC Address =\n " + newDevice.getAddress();
                    Toast.makeText(context, device, Toast.LENGTH_LONG).show();

                    DiscoveredDevices.add(newDevice);
                    DiscoveredDevicesArray.add(device);

                    DiscoveredArrayAdapter.notifyDataSetChanged();
                }

            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(context, "Discovery Started", Toast.LENGTH_LONG).show();
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Toast.makeText(context, "Discovery Finished", Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void closeConnection() {
        try {
            mmInStream.close();
            mmInStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mmOutStream.close();
            mmOutStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mmSocket.close();
            mmSocket = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        isConnected = false;
    }

    public void CreateSerialBluetoothDeviceSocket(BluetoothDevice device) {
        mmSocket = null;

        UUID DEFAULT_SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        try {
            mmSocket = device.createRfcommSocketToServiceRecord(DEFAULT_SPP_UUID);
        } catch (IOException e) {
            Toast.makeText(context, "Socket Creation Failed", Toast.LENGTH_LONG).show();
        }
    }

    public void ConnectToSerialBlueToothDevice() {
        mBluetoothAdapter.cancelDiscovery();

        try {
            mmSocket.connect();
            Toast.makeText(context, "Connection Made", Toast.LENGTH_LONG).show();
        } catch (IOException connectException) {
            Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
            return;
        }

        GetInputOutputStreamsForSocket();
        isConnected = true;
    }

    public void GetInputOutputStreamsForSocket() {
        try {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT)
            if(resultCode != RESULT_OK) {
                Toast.makeText(this, "Bluetooth failed to start", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

    }

    private AdapterView.OnItemClickListener mDiscoveredClickedHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = "Discovered Device: " + DiscoveredDevicesArray.get(position);
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

            if (isConnected)
                closeConnection();

            CreateSerialBluetoothDeviceSocket(DiscoveredDevices.get(position));
            ConnectToSerialBlueToothDevice();

            DiscoveredArrayAdapter.setValid(position);
            DiscoveredArrayAdapter.notifyDataSetChanged();
        }
    };

    private AdapterView.OnItemClickListener mPairedClickedHandler= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = "Paired Device: " + PairedDevicesArray.get(position);
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

            if (isConnected)
                closeConnection();

            CreateSerialBluetoothDeviceSocket(PairedDevices.get(position));
            ConnectToSerialBlueToothDevice();

            PairedArrayAdapter.setValid(position);
            PairedArrayAdapter.notifyDataSetChanged();
        }
    };

    public void WritetoBTDevice(String message) {
        String s = new String("\r\n");

        byte[] msgBuffer = message.getBytes();
        byte[] newline = s.getBytes();

        try {
            mmOutStream.write(msgBuffer);
            mmOutStream.write(newline);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String ReadFromBTDevice() {
        byte c;
        String s = new String("");

        try {
            for (int i = 0; i < 200; i++) {
                SystemClock.sleep(10);
                if (mmInStream.available() > 0) {
                    if ((c = (byte) mmInStream.read()) != '\r') {
                        s += (char) c;
                    } else {
                        return s;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new String("--No Response--");
    }

}
