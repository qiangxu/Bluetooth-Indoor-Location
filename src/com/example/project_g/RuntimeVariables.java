package com.example.project_g;

import java.util.ArrayList;

public class RuntimeVariables {
    private static RuntimeVariables instance;
    private String[] selectedDevices;
    private ArrayList<Short[]> RSSI;
    private ArrayList<Short[]> position;
    
    private RuntimeVariables() {}
    
    public static RuntimeVariables instance() {
        if(instance == null)
            instance = new RuntimeVariables();
        return instance;
    }
    
    public void setSelectedDevice(String[] addresses) {
        selectedDevices = addresses;
    }
    
    public String[] getSelectedDevices() {
        return selectedDevices;
    }
    
    public void addRSSI(Short[] rssi) {
        if(RSSI == null)
            RSSI = new ArrayList<Short[]>();
        RSSI.add(rssi);
    }
    
    public ArrayList<Short[]> getRSSI() {
        return RSSI;
    }
    
    public void addPosition(Short x, Short y) {
        if(position == null)
            position = new ArrayList<Short[]>();
        Short[] posXY = {x, y};
        RSSI.add(posXY);
    }
    
    public ArrayList<Short[]> getPosition() {
        return position;
    }
}
