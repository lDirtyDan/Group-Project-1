package edu.jsu.mcis.tas_fa19;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
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
    
    public String getWeekDayShort(LocalDateTime originalTimeStamp){
        String weekDay = null;
        weekDay = originalTimeStamp.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US).toUpperCase();
        
        /*if(weekDay.contains("WED")){
           weekDay = "WED";
        }*/
        
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
        
        return "#" + badge.getId() + " " + adjustmenttype + ": " + getWeekDayShort(originalTimeStamp).toUpperCase() +" "+ formatDate(originalTimeStamp) + formatTime(originalTimeStamp);
    }
    
    
    
}
