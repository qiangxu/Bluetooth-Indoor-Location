package com.example.project_g;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_g.ui.MultiSelectDialog;
import com.example.project_g.ui.MultiSelectDialog.OnSelectedListener;
import com.example.project_g.ui.PromptDialog;
import com.example.project_g.ui.PromptDialog.OnInputListener;

public class MainActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 2;
    private BluetoothAdapter mBluetoothAdapter;
    private RuntimeVariables rv;
    
    private Short[] RSSI; // Temporary rssi storage variable
    private Short posX; // Temporary position storage variable
    private Short posY; // Temporary position storage variable
    
    private TextView tvLog;
    private MultiSelectDialog multiSelectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        tvLog = (TextView)findViewById(R.id.textview_log);
        
        rv = RuntimeVariables.instance();
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "The device doesn't support bluetooth.", Toast.LENGTH_SHORT).show();
            finish();
        }
        
        initBroadcastReceivers();
        
        // Open Bluetooth
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // Start Scan
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == REQUEST_ENABLE_BT) {
            // Start Scan
            mBluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onDestroy() {
        if(mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.disable();
        super.onDestroy();
    }
    
    private void log(String[] devices) {
        for (int i = 0; i < devices.length; i++) {
            tvLog.setText(tvLog.getText() + devices[i] + "\t");
        }
        tvLog.setText(tvLog.getText() + "\n");
    }

    private void log(Short[] rssi) {
        for (int i = 0; i < rssi.length; i++) {
            tvLog.setText(tvLog.getText() + String.valueOf(rssi[i]) + "dBm\t");
        }
        tvLog.setText(tvLog.getText() + "\n");
    }
    
    private void log(Short x, Short y) {
        tvLog.setText(tvLog.getText() + "(" + String.valueOf(x) + ", " + String.valueOf(y) + ")" + "\n");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_scan:
            if(mBluetoothAdapter.isDiscovering())
                Toast.makeText(this, "Program is already discovering bluetooth signal sources, please wait.", Toast.LENGTH_SHORT).show();
            else {
                // Start Scan
                Toast.makeText(this, "Discovering bluetooth signal sources, please wait.", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.startDiscovery();
            }
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void initBroadcastReceivers() {
        
        Toast.makeText(this, "Discovering bluetooth signal sources, please wait.", Toast.LENGTH_SHORT).show();
        

        // Register the Bluetooth Discovery Started BroadcastReceiver
        IntentFilter sFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if(rv.getSelectedDevices() != null)
                    RSSI = new Short[rv.getSelectedDevices().length];
            }
            
        }, sFilter);
            
            // Register the Bluetooth Information BroadcastReceiver
            IntentFilter iFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    String name = device.getName(); // Bluetooth device name
                    String address = device.getAddress(); // Bluetooth device name
                    short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI); // Bluetooth device signal intensity
                    
                    processBluetoothInfo(name, address, rssi);
                    
                    Toast.makeText(MainActivity.this, "Found " + name +  "(" + address + "), " + rssi + "dBm", Toast.LENGTH_SHORT).show();
                }
            }
        }, iFilter);
        
        // Register the Bluetooth Discovery Finished BroadcastReceiver
        IntentFilter dFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                PromptDialog dialogX = new PromptDialog();
                dialogX.setTitle("Please input x, y");
                dialogX.setOnInputListener(new OnInputListener() {
                    
                    @Override
                    public void onInput(String x, String y) {
                        try {
                            posX = Short.parseShort(x);
                            posY = Short.parseShort(y);
                            rv.addPosition(posX, posY);
                            log(posX ,posY);
                        } catch (Exception e) {}
                    }
                });
                dialogX.build(MainActivity.this).show();
                // Build dialog only for the first time
                if(rv.getSelectedDevices() == null) {
                    multiSelectDialog.build(MainActivity.this).show();
                } else {
                    rv.addRSSI(RSSI);
                    log(RSSI);
                }
            }
        }, dFilter);
    }
    
    public void processBluetoothInfo(String name, String address, short rssi) {
        String[] selectedDevices = rv.getSelectedDevices();
        // Build dialog only for the first time
        if(selectedDevices == null) {
            final ArrayList<Map<String, String>> bluetoothList = new ArrayList<Map<String,String>>();
            multiSelectDialog = new MultiSelectDialog();
            multiSelectDialog.setTitle("Select sources you want to record");
            multiSelectDialog.setOnClickListener(new OnSelectedListener() {
                
                @Override
                public void onSelected(boolean[] selection) {
                    ArrayList<String> address = new ArrayList<String>();
                    ArrayList<Short> rssi = new ArrayList<Short>();
                    for (int i = 0; i < selection.length; i++) {
                        if(selection[i]) {
                            address.add(bluetoothList.get(i).get("address"));
                            rssi.add(Short.parseShort(bluetoothList.get(i).get("rssi")));
                        }
                    }
                    String[] addresses = new String[address.size()];
                    Short[] rssis = new Short[address.size()];
                    for (int i = 0; i < addresses.length; i++) {
                        addresses[i] = address.get(i);
                        rssis[i] = rssi.get(i);
                    }
                    rv.setSelectedDevice(addresses);
                    rv.addRSSI(rssis);
                    log(addresses);
                    log(rssis);
                }
            });
            multiSelectDialog.addItem(name + "(" + address + "), " + rssi + "dBm");
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("address", address);
            map.put("rssi", String.valueOf(rssi));
            bluetoothList.add(map);
        } else {
            for (int i = 0; i < selectedDevices.length; i++) {
                if(selectedDevices[i].equals(address)) {
                    RSSI[i] = rssi;
                }
            }
        }
    }
    
}
