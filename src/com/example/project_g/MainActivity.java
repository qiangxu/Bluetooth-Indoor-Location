package com.example.project_g;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_g.ui.MultiSelectDialog;
import com.example.project_g.ui.MultiSelectDialog.OnSelectedListener;
import com.example.project_g.ui.PromptDialog;
import com.example.project_g.ui.PromptDialog.OnInputListener;

public class MainActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 2;
    private boolean isCalculating = false;
    
    private BluetoothAdapter mBluetoothAdapter;
    private SignalInfoRecord tmpRecord;

    private Map<String, Short> tmpScanMap;
    private ArrayList<String> selectedDevices;
    private ArrayList<SignalInfoRecord> allSignalInfoRecords;
    private ArrayList<SignalInfoRecord> selectedSignalInfoRecords;
    
    private List<Double> resultList; // ��������������Ȩ��ֵ
    private Map<Double, SignalInfoRecord> resultMap;// ��������������λ�÷���͵�

    private MenuItem menuScan;
    private MenuItem menuCalc;
    private TextView tvLog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        tvLog = (TextView)findViewById(R.id.textview_log);
        
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(this, "The device doesn't support bluetooth.", Toast.LENGTH_SHORT).show();
            finish();
        }
        
        // Open Bluetooth
        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        
        allSignalInfoRecords = new ArrayList<SignalInfoRecord>();
        
        // Discovery Started
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                log("Discovering bluetooth signal sources, please wait.");

                if(isCalculating)
                    tmpScanMap = new HashMap<String, Short>();
                else
                    tmpRecord = new SignalInfoRecord();
                
                menuScan.setEnabled(false);
                menuCalc.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
            }
            
        }, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
            
        // Bluetooth Information BroadcastReceiver
        registerReceiver(new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String name = device.getName();
                    String address = device.getAddress();
                    short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                    
                    if(isCalculating)
                        tmpScanMap.put(address, rssi);
                    else
                        tmpRecord.addInfo(new SignalInfo(name, address, rssi));
                    
                    log("Found " + name +  "(" + address + "), " + rssi + "dBm");
                }
            }
        }, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        
        // Discovery Finished
        registerReceiver(new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                menuScan.setEnabled(true);
                menuCalc.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if(!isCalculating) {
                    PromptDialog dialog = new PromptDialog();
                    dialog.setTitle("Please input current position:(x,y)");
                    dialog.setOnInputListener(new OnInputListener() {
                        
                        @Override
                        public void onInput(String x, String y) {
                            try {
                                short posX = Short.parseShort(x);
                                short posY = Short.parseShort(y);
                                tmpRecord.setPosition(posX, posY);
                                allSignalInfoRecords.add(tmpRecord);
                            } catch (Exception e) {
                                log("Illegal input! This scan will not be recorded.");
                            }
                        }
                    });
                    dialog.build(MainActivity.this).show();
                } else {
                    startCalculations();
                }
            }
        }, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == REQUEST_ENABLE_BT)
            log("Bluetooth started.");
    }

    @Override
    protected void onDestroy() {
        if(mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.disable();
        super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); 
        inflater.inflate(R.menu.menu, menu);
        menuScan = menu.findItem(R.id.menu_scan);
        menuCalc = menu.findItem(R.id.menu_calc);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_scan:
            mBluetoothAdapter.startDiscovery();
            break;
        case R.id.menu_calc:
            selectDevice();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private void log(String text) {
        tvLog.setText(tvLog.getText() + text + "\n");
    }
    
    private void selectDevice() {
        final ArrayList<SignalInfo> signalInfoList = new ArrayList<SignalInfo>();
        MultiSelectDialog dialog = new MultiSelectDialog();
        if(allSignalInfoRecords == null) {
            log("No valid bluetooth information!");
            return;
        }
        dialog.setTitle("Please select device");
        for (SignalInfoRecord record : allSignalInfoRecords) {
            for (SignalInfo signalInfo : record.infos()) {
                boolean flag = true;
                for (SignalInfo signal : signalInfoList) {
                    if(signal.address().equals(signalInfo.address())) {
                        flag = false;
                        break;
                    }
                }
                if(flag) {
                    signalInfoList.add(signalInfo);
                    dialog.addItem(signalInfo.name() + "(" + signalInfo.address() + ")");
                }
            }
        }
        if(signalInfoList.size() == 0) {
            log("No valid bluetooth information!");
            return;
        }
        dialog.setOnSelectedListener(new OnSelectedListener() {
            
            @Override
            public void onSelected(boolean[] selection) {
                selectedDevices = new ArrayList<String>();
                for (int i = 0; i < selection.length; i++) {
                    if(selection[i])
                        selectedDevices.add(signalInfoList.get(i).address());
                }
                parserData();
                isCalculating = true;
                mBluetoothAdapter.startDiscovery();
            }
        });
        dialog.build(this).show();
    }
    
    private void parserData() {
        log("Start realtime information gathering...");
        selectedSignalInfoRecords = new ArrayList<SignalInfoRecord>();
        for (SignalInfoRecord record : allSignalInfoRecords) {
            SignalInfoRecord newRecord = new SignalInfoRecord();
            for (String selectedAddress : selectedDevices) {
                for (SignalInfo signal : record.infos()) {
                    if(signal.address().equals(selectedAddress))
                        newRecord.addInfo(signal);
                }
            }
            if(newRecord.infos().size() > 0) {
                newRecord.setPosition(record.position().x, record.position().y);
                selectedSignalInfoRecords.add(newRecord);
            }
        }
    }
    
    private void startCalculations() {
        log("Start calculation.");
        resultList = new ArrayList<Double>();
        resultMap = new HashMap<Double, SignalInfoRecord>();

        if (selectedSignalInfoRecords != null && selectedSignalInfoRecords.size() > 0) {
            for (int i = 0; i < selectedSignalInfoRecords.size(); i++) {
                SignalInfoRecord signalInfoRecord = selectedSignalInfoRecords.get(i);

                // ÿ�μ�����ľ���
                double doDistance = calcDistance(signalInfoRecord.infos());

                if (doDistance != 0.0) {
                    // ����ÿ�����������ľ���
                    resultList.add(doDistance);
                    // ����ÿ������������λ�� ����͵� x��y
                    resultMap.put(doDistance, signalInfoRecord);
                }
            }
        }
        getPosition();
    }
    
    private double calcDistance(ArrayList<SignalInfo> signalInfos) {
        double nowDistance = 0;
        if (tmpScanMap != null) {
            for (int i = 0; i < signalInfos.size(); i++) {
                SignalInfo signalInfo = signalInfos.get(i);
                if (tmpScanMap.containsKey(signalInfo.address())) {
                    // ��ĳ���ϼ�⵽��rssi���¼�е�һ��ʱ��Ȩ������x
                    // �õ��Ӧ����ʷ��¼���ź�ǿ��
                    int xmlLevel = Integer.valueOf(tmpScanMap.get(signalInfo.address()));
                    // �õ���ź�ǿ��
                    int nowLevel = signalInfo.rssi();
                    // ��������
                    int newLevel = Math.abs(xmlLevel - nowLevel);
                    // ����ƽ����
                    nowDistance += Math.pow(newLevel, 2);
                }
            }
        }

        return Math.sqrt(nowDistance);
    }
    
    private void getPosition() {
        // ȡ����С���������
        if(resultList.size() > 0) {
            Collections.sort(resultList);
            double zx = 0;
            double zy = 0;
            int size = (resultList.size() > 5 ? 5 : resultList.size());
            // ������С���x��y���ܺ�
            for (int i = 0; i < size; i++) {
                zx = zx + Double.valueOf(resultMap.get(resultList.get(i)).position().x);
                zy = zy + Double.valueOf(resultMap.get(resultList.get(i)).position().y);
            }

            zx = zx / 5;
            zy = zy / 5;
    
            log("Current position:" + "x:" + zx + ", y:" + zy);
        }
    }
    
}
