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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.securify.ui.adapters.DeviceArrayAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
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

        checkPermission();

    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice newDevice;

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {

                newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!DiscoveredDevices.contains(newDevice) && !PairedDevices.contains(newDevice)) {
                    String device = newDevice.getName() + "\nMAC Address =\n " + newDevice.getAddress();
                    Toast.makeText(context, device, Toast.LENGTH_SHORT).show();

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

        BluetoothStreams.getInstance().setInputStream(mmInStream);
        BluetoothStreams.getInstance().setOutputStream(mmOutStream);

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
        Log.d("BTActivity", "I/O Cleared");
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

    public void ConnectToSerialBlueToothDevice(BluetoothDevice device) {
        mBluetoothAdapter.cancelDiscovery();

        try {
            mmSocket.connect();
            Toast.makeText(context, "Connection Made", Toast.LENGTH_LONG).show();
        } catch (IOException connectException) {
            try {
                mmSocket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,1);
                mmSocket.connect();
                Toast.makeText(context, "Connection Made", Toast.LENGTH_LONG).show();
            } catch (IllegalAccessException | IOException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                Toast.makeText(context, "Connection Failed", Toast.LENGTH_LONG).show();
                return;
            }

        }

        GetInputOutputStreamsForSocket();
        isConnected = true;
    }

    public void GetInputOutputStreamsForSocket() {
        try {
            mmInStream = mmSocket.getInputStream();
            mmOutStream = mmSocket.getOutputStream();
            Log.d("BTActivity", "I/O Success");
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

    private final AdapterView.OnItemClickListener mDiscoveredClickedHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = "Discovered Device: " + DiscoveredDevicesArray.get(position);
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

            if (isConnected)
                closeConnection();

            CreateSerialBluetoothDeviceSocket(DiscoveredDevices.get(position));
            ConnectToSerialBlueToothDevice(DiscoveredDevices.get(position));

            DiscoveredArrayAdapter.setValid(position);
            DiscoveredArrayAdapter.notifyDataSetChanged();
        }
    };

    private final AdapterView.OnItemClickListener mPairedClickedHandler= new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String text = "Paired Device: " + PairedDevicesArray.get(position);
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();

            if (isConnected)
                closeConnection();

            CreateSerialBluetoothDeviceSocket(PairedDevices.get(position));
            ConnectToSerialBlueToothDevice(PairedDevices.get(position));

            PairedArrayAdapter.setValid(position);
            PairedArrayAdapter.notifyDataSetChanged();
        }
    };


    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    123);
        } else {
            setUpBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 123) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setUpBluetooth();
            } else {
                // TODO: Explain to the user that the feature is unavailable because
                // the features requires a permission that the user has denied.
                // At the same time, respect the user's decision. Don't link to
                // system settings in an effort to convince the user to change
                // their decision.
                finish();
            }
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    public String ReadFromBTDevice() {
        byte c;
        String s = "";

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

        return "--No Response--";
    }

    private void setUpBluetooth() {
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


}
