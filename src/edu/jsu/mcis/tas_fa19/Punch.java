package edu.jsu.mcis.tas_fa19;

import java.text.SimpleDateFormat;
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
        int counter = 0;
        ArrayList<GregorianCalendar> timeCheck = new ArrayList<GregorianCalendar>();
        ArrayList<LocalTime> timeClock = new ArrayList<LocalTime>();
        LocalTime start = adjShift.getStart();
        LocalTime stop = adjShift.getStop();
        LocalTime lunchStart = adjShift.getLunchStart();
        LocalTime lunchStop = adjShift.getLunchStop();
        
        timeClock.add(start.minusMinutes(dock));        // ID = 0 before Clock-in
        timeClock.add(start);                           // ID = 1 Clock-in
        timeClock.add(start.plusMinutes(grace));        // ID = 2 Clock-in grace period
        timeClock.add(start.plusMinutes(dock));         // ID = 3 Clock-in late
        timeClock.add(lunchStart);                      // ID = 4 Lunch Starts
        timeClock.add(lunchStop);                       // ID = 5 Lunch Stops
        timeClock.add(stop.minusMinutes(dock));         // ID = 6 early Clock-out
        timeClock.add(stop.minusMinutes(grace));        // ID = 7 Clock-out grace period
        timeClock.add(stop);                            // ID = 8 Clock-out
        timeClock.add(stop.plusMinutes(dock));          // ID = 9 Clock-out late
        
        while(counter != 10){
            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal = originalTimeStamp;
            
            tempCal.set(Calendar.HOUR_OF_DAY, timeClock.get(counter).getHour());
            tempCal.set(Calendar.MINUTE, timeClock.get(counter).getMinute());
            tempCal.set(Calendar.SECOND, timeClock.get(counter).getSecond());
            
            timeCheck.add(tempCal);
            String timeStamp = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(timeCheck.get(counter).getTime());
            // System.out.println( "Punch ID: "+ adjShift.getId() + " Time: " + timeStamp); // Uncomment this to output PunchID and Timestamp
            counter++;
        }
      
        
        adjustedTimeStamp = originalTimeStamp; 
        adjustedTimeStamp.set(Calendar.SECOND, 0);
        
        
        /*V If statement nest to determine where a stamp should be adjusted and how. V*/
        if(punchtypeid == 1){
            if(0<=adjustedTimeStamp.compareTo(timeCheck.get(0)) && adjustedTimeStamp.compareTo(timeCheck.get(1))<0)         // before clock-in
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(1).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(1).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(1).getTime())));
            }
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(1)) && adjustedTimeStamp.compareTo(timeCheck.get(2))<0)    // after clock-in, but w/ grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(1).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(1).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(1).getTime())));
            }
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(2)) && adjustedTimeStamp.compareTo(timeCheck.get(3))<0)    // after clock-in w/o grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(3).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(3).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(3).getTime())));
            }
            
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(4)) && adjustedTimeStamp.compareTo(timeCheck.get(5))<0)    // Lunch Check clock in
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(5).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(5).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(5).getTime())));
            }
            
            /*
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(3)) && adjustedTimeStamp.compareTo(timeCheck.get(4))<0)    // auto interval adjustment
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(1).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(1).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(1).getTime())));
            }
            */
            
        }
        
        else if(punchtypeid == 0){
            
            if(0<=adjustedTimeStamp.compareTo(timeCheck.get(4)) && adjustedTimeStamp.compareTo(timeCheck.get(5))<0)    // Lunch Check clock out
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(4).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(4).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(4).getTime())));
            }
    
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(6)) && adjustedTimeStamp.compareTo(timeCheck.get(7))<0)    // Clock-out w/o grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(6).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(6).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(6).getTime())));
            }
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(7)) && adjustedTimeStamp.compareTo(timeCheck.get(8))<0)    // clock out w/ grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(8).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(8).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(8).getTime())));
            }
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(8)) && adjustedTimeStamp.compareTo(timeCheck.get(9))<0)    // clock-out late
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(8).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(8).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(8).getTime())));
            }
            
            /*
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(5)) && adjustedTimeStamp.compareTo(timeCheck.get(6))<0)    // interval check
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(1).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(1).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(1).getTime())));
            }
            */
        }
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
        
        //clock in is defined by 1
        //clock out is defined by 0
        //punchID is the variable for the punchtype
        //punchTime holds the time that punch is made 
        
        /*
        //clocking in if statements
        if punchID == 1 {
            
            if punchTime > earlystart && punchTime < start {
                punchTime = start;
            }
            
            else if punchTime = start {
                punchTime = start;
            }
        
            else if punchTime > start && punchTime <= ingraceperiod {
                punchTime = start;
            }
        
            else if punchTime > lunchstart && punchTime <= lunchstop
                punchTime = lunchstop;
            }
        
        }
        
        //clocking out if statements
        if punchID == 0 {
            
            if punchTime >= outgraceperiod && punchTime < stop {
                punchTime = stop;
            }
        
            else if punchTime == stop {
                punchTime = stop;
            }
        
            else if punchTime > stop && punchTime < latestop {
                punchTime = stop;
            }
            
            //lunch 
            else if punchTime >= lunchstart && punchTime < lunchstop {
                punchTime = lunchstart;
            }
        
        }) 
           
        */   
    }
    
    public String printAdjustedTimestamp(){
         
        //return "#" + badge.getId() + " " + adjustmenttype + ": " + getWeekDayShort(LDTTimeStamp).toUpperCase() +" "+ formatDate(LDTTimeStamp) + formatTime(LDTTimeStamp);
    
        return null;
    }
    
}
