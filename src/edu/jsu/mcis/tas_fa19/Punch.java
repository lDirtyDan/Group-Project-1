package edu.jsu.mcis.tas_fa19;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Punch {
    private Badge badge = null;
    private int terminalid;
    private int punchtypeid;
    private GregorianCalendar originalTimeStamp = new GregorianCalendar();
    private LocalDateTime LDTTimeStamp = null;
    private int id = 0;
    private String adjustmenttype = null;
    
    public Punch(Badge badge, int terminalid, int punchtypeid){
        this.badge = badge;
        this.terminalid = terminalid;
        this.punchtypeid = punchtypeid;
        
        /*changes adjustmenttype to specified string based punchtypeid*/
        switch(punchtypeid){
            case 0: adjustmenttype = "CLOCKED OUT";
                    break;
            case 1: adjustmenttype = "CLOCKED IN";
                    break;
            case 2: adjustmenttype = "TIMED OUT";
                    break;
        }
    }

    public Badge getBadge() {
        return badge;
    }
    
    public String getBadgeid(){
        if(badge != null){
            return badge.getId();
        }
        return "Incorrect";
    }

    public int getTerminalid() {
        return terminalid;
    }

    public int getPunchtypeid() {
        return punchtypeid;
    }

    public long getOriginaltimestamp() {
        return originalTimeStamp.getTimeInMillis();
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

    public void setOriginalTimeStamp(long timeStamp) {
        originalTimeStamp.setTimeInMillis(timeStamp);
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public void setAdjustmenttype(String adjustmenttype) {
        if(adjustmenttype != null){
            this.adjustmenttype = adjustmenttype;
        }
    }
    
    private LocalDateTime longToLocalDateTime(long longTime){
        LocalDateTime timeStamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(longTime), TimeZone.getDefault().toZoneId());
        return timeStamp;
    }
    
    public String getWeekDayShort(LocalDateTime originalTimeStamp){
        String weekDay = null;
        weekDay = originalTimeStamp.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US).toUpperCase();
        
        return weekDay;
    }
    
    public String formatDate (LocalDateTime originalTimeStamp){
        String yearText = null;
        String monthText = null;
        String dayText = null;
        
        int yearValue = originalTimeStamp.getYear();
        int monthValue = originalTimeStamp.getMonthValue();
        int dayValue = originalTimeStamp.getDayOfMonth();
        
        if(monthValue < 10){
            monthText = "0" + monthValue;
        }
        else{
            monthText = "" + monthValue;
        }
        
        if(dayValue < 10){
            dayText = "0" + dayValue;
        }
        else{
            dayText = "" + dayValue;
        }
            yearText = "" + yearValue + " ";
        
        return monthText + "/" + dayText + "/" + yearText;
    }
    
    public String formatTime(LocalDateTime originalTimeStamp){
        int second = originalTimeStamp.getSecond();
        String secText = null;
        String locTime = originalTimeStamp.toLocalTime().toString();
        String finTime = null;
        
        if(locTime.length() < 8){
            if(second < 10){
                secText = ":0" + second;
            }
            else{
                secText = ":" + second;
            }
            finTime = locTime + secText;
        }
        else{
            finTime = locTime;
        }
        
        return finTime;
    }


    public String printOriginalTimestamp() {
         long timeLong = originalTimeStamp.getTimeInMillis();
         timeLong = timeLong/100;
         LDTTimeStamp = longToLocalDateTime(timeLong);
         
        return "#" + badge.getId() + " " + adjustmenttype + ": " + getWeekDayShort(LDTTimeStamp).toUpperCase() +" "+ formatDate(LDTTimeStamp) + formatTime(LDTTimeStamp);
        
    }
    
    
    //Feature 3
    public void adjust(Shift s){
        //use varibles that is created in "shift.java"
        
        //create badgeID variable
        //create punch varible
        
        //varibles for shift
            //start shift
            //grace period
            //docks (early & late)
            //shift end
            //lunch start
            //lunch stop
            
        //pull "punch" from database
        //pull "badgeID" from punch class
        //pull "shiftID" from badgeid
        //multiple "if" startments to compare each shift type
        //make adjustments based on time
    }
    
}