package com.example.project_g;

import java.util.ArrayList;

public class SignalInfoRecord {
    
    public class Position {
        public short x;
        public short y;
        
        public Position(short x, short y) {
            this.x = x;
            this.y = y;
        }
    }
    
    private ArrayList<SignalInfo> infos;
    private Position position;
    
    public void addInfo(SignalInfo info) {
        if(this.infos == null)
            this.infos = new ArrayList<SignalInfo>();
        this.infos.add(info);
    }
    
    public void setPosition(short x, short y) {
        this.position = new Position(x, y);
    }
    
    public ArrayList<SignalInfo> infos() {
        return this.infos;
    }
    
    public Position position() {
        return this.position;
    }
}
