package com.example.project_g;

public class SignalInfo {

    private String name;
    private String address;
    private short rssi;
    
    public SignalInfo() {}
    
    public SignalInfo(String name, String address, short rssi) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public void setRssi(short rssi) {
        this.rssi = rssi;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getAddress() {
        return this.address;
    }
    
    public short getRssi(){
        return this.rssi;
    }

}
