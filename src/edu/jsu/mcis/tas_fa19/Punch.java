package edu.jsu.mcis.tas_fa19;


public class Punch {
    private Badge badge;
    private int terminalid;
    private int punchtypeid;
    private long originalTimeStamp = 0;
    private int id = 0;
    private String adjustmenttype;
    
    public Punch(Badge badge, int terminalid, int punchtypeid){
        this.badge = badge;
        this.terminalid = terminalid;
        this.punchtypeid = punchtypeid;
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

    public long getOriginalTimeStamp() {
        return originalTimeStamp;
    }

    public int getId() {
        return id;
    }

    
    
    public String getAdjustmenttype() {
        return adjustmenttype;
    }
    
    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public void setTerminalid(int terminalid) {
        this.terminalid = terminalid;
    }

    public void setPunchtypeid(int punchtypeid) {
        this.punchtypeid = punchtypeid;
    }

    public void setOriginalTimeStamp(long originalTimeStamp) {
        this.originalTimeStamp = originalTimeStamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAdjustmenttype(String adjustmenttype) {
        this.adjustmenttype = adjustmenttype;
    }

    public String printOriginalTimestamp() {
        return adjustmenttype + " : " + originalTimeStamp;
    }
    
    
    
}
