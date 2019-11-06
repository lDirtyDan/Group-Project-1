package edu.jsu.mcis.tas_fa19;

import java.util.ArrayList;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.time.LocalTime;
import java.util.Calendar;

public class Punch {
    private Badge badge = null;
    private int terminalid;
    private int punchtypeid;
    private GregorianCalendar originalTimeStamp = new GregorianCalendar();
    private GregorianCalendar adjustedTimeStamp = new GregorianCalendar();
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
         LocalDateTime LDTTimeStamp = longToLocalDateTime(timeLong);
         
        return "#" + badge.getId() + " " + adjustmenttype + ": " + getWeekDayShort(LDTTimeStamp).toUpperCase() +" "+ formatDate(LDTTimeStamp) + formatTime(LDTTimeStamp);
        
    }
    
    
    //Feature 3
    public void adjust(Shift s){
        Shift adjShift = s; // sets shift to a local shift object
        
        /*V brings shift objects to local objects for easier usage V*/
        int interval = adjShift.getInterval();
        int grace = adjShift.getGracePeriod();
        int dock = adjShift.getDock();
        ArrayList<LocalTime> timeClock = new ArrayList<LocalTime>();
        LocalTime start = adjShift.getStart();
        LocalTime stop = adjShift.getStop();
        LocalTime lunchStart = adjShift.getLunchStart();
        LocalTime lunchStop = adjShift.getLunchStop();
        timeClock.add(start);
        timeClock.add(stop);
        timeClock.add(lunchStart);
        timeClock.add(lunchStop);

        ArrayList<GregorianCalendar> timeCheck = new ArrayList<GregorianCalendar>();
        
        
        int hour = 0;
        int minute = 0;
        int second = 0;
        
        adjustedTimeStamp = originalTimeStamp;
        
        
        
        if(punchtypeid == 0){
        
        }
        if(punchtypeid == 1){
        
        }
        if(punchtypeid == 2){
        
        }
        
        adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, hour);
        adjustedTimeStamp.set(Calendar.MINUTE, minute);
        adjustedTimeStamp.set(Calendar.SECOND, second);
        
        
        
        
        //varibles for shift
            //start shift
            //grace period
            //docks (early & late)
            //shift end
            //lunch start
            //lunch stop
            
            //int dock = starttime
            
        //pull "badgeID" from punch class
        //pull "shiftID" from badgeid
        //multiple "if" startments to compare each shift type
        //make adjustments based on time
        
        /*
        // start of shift
        
        if punch >= earlyshiftstart || punch < shiftstart {
            punch = shiftstart;
        }
        
        else if punch = shifttime {
            punch = shiftstart;
        }
        
        else if punch > shiftstart || punch <= graceperiod {
            punch = starttime;
        }
        
        else if punch > graceperiod || punch <= firstdockinterval {
            punch = roll(dock, 15) (7:15); 
        }
        
        else if punch > firstdockinterval || punch <= seconddockinterval - interval * .5  {
            punch = roll(dock, 15) (7:15);
        }
        
        else if punch > seconddockinterval - intercal * .5 && punch < seconddockinterval {
            punch = roll(dock, 30) (7:30);
        }
        
        //lunch start
         
        else if punch = lunchstart {
            punch = lunchstart;
        }
        
                
        */
           
    }
    
    public String printAdjustedTimestamp(){
         
        //return "#" + badge.getId() + " " + adjustmenttype + ": " + getWeekDayShort(LDTTimeStamp).toUpperCase() +" "+ formatDate(LDTTimeStamp) + formatTime(LDTTimeStamp);
    
        return null;
    }
    
}
