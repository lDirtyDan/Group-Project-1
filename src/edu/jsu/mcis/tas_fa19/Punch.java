package edu.jsu.mcis.tas_fa19;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class Punch {
    private Badge badge = null;
    private int terminalid;
    private int punchtypeid;
    private LocalDateTime originalTimeStamp = null;
    private int id = 0;
    private String adjustmenttype = null;
    
    public Punch(Badge badge, int terminalid, int punchtypeid){
        this.badge = badge;
        this.terminalid = terminalid;
        this.punchtypeid = punchtypeid;
        
        /*changes adjustmenttype to specified string based punchtypeid*/
        switch(punchtypeid){
            case 0: adjustmenttype = "CLOCKED OUT";
            case 1: adjustmenttype = "CLOCKED IN";
            case 2: adjustmenttype = "TIMED OUT";
        }
    }

    public Badge getBadge() {
        return badge;
    }

    public int getTerminalid() {
        return terminalid;
    }

    public int getPunchtypeid() {
        return punchtypeid;
    }

    public LocalDateTime getOriginalTimeStamp() {
        return originalTimeStamp;
    }

    public int getId() {
        return id;
    }

    
    
    public String getAdjustmenttype() {
        return adjustmenttype;
    }
    
    public void setBadge(Badge badge) {
        if(badge != null){
            this.badge = badge;
        }
    }

    public void setTerminalid(int terminalid) {
        this.terminalid = terminalid;
    }

    public void setPunchtypeid(int punchtypeid) {
        this.punchtypeid = punchtypeid;
    }

    public void setOriginalTimeStamp(LocalDateTime originalTimeStamp) {
        if(originalTimeStamp != null){
            this.originalTimeStamp = originalTimeStamp;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAdjustmenttype(String adjustmenttype) {
        if(adjustmenttype != null){
            this.adjustmenttype = adjustmenttype;
        }
    }

    public String printOriginalTimestamp() {
        return "#" + badge.getId() + " " + adjustmenttype + ": " + originalTimeStamp;
    }
    
    
    
}
