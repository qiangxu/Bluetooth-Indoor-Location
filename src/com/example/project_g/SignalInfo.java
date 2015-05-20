package com.example.project_g;

public class SignalInfo {

    private String name;
    private String address;
    private short rssi;
    
    public SignalInfo(String name, String address, short rssi) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
    }
    
    public void name(String name) {
        this.name = name;
    }
    
    public void address(String address) {
        this.address = address;
    }
    
    public void rssi(short rssi) {
        this.rssi = rssi;
    }
    
    public String name() {
        return this.name;
    }
    
    public String address() {
        return this.address;
    }
    
    public short rssi(){
        return this.rssi;
    }

}
