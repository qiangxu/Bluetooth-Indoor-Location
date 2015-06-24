package com.example.project_g;

import java.util.ArrayList;
import java.util.List;

public class SignalInfoRecord {
    
    public class Position {
        public short x;
        public short y;
        
        public Position() {}
        
        public Position(short x, short y) {
            this.x = x;
            this.y = y;
        }
        
        public short getX() {
            return x;
        }
        
        public short getY() {
            return y;
        }
        
        public void setX(short x) {
            this.x = x;
        }
        
        public void setY(short y) {
            this.y = y;
        }
    }
    
    private List<SignalInfo> infos;
    private Position position;
    
    public void setPosition(Position pos) {
        this.position = pos;
    }
    
    public void setPosition(short x, short y) {
        this.position = new Position(x, y);
    }
    
    public void setInfos(List<SignalInfo> infos) {
        this.infos = infos;
    }
    
    public List<SignalInfo> getInfos() {
        if(this.infos == null)
            this.infos = new ArrayList<SignalInfo>();
        return this.infos;
    }
    
    public Position getPosition() {
        return this.position;
    }
}
