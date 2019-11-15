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
    public static final int CLOCK_OUT = 0;
    public static final int CLOCK_IN = 1;
    public static final int TIME_OUT = 2;
    public static final String[] punchDescriptions = new String[]{"CLOCKED OUT", "CLOCKED IN", "TIMED OUT"};
    private Badge badge = null;
    private int terminalid;
    private int punchtypeid;
    private long originaltimestamp;
    private long adjustedtimestamp;
    private int id = 0;
    private String adjustmenttype = null;
    private String adjType;
    
    
    public Punch(Badge badge, int terminalid, int punchtypeid){
        this.badge = badge;
        this.terminalid = terminalid;
        this.punchtypeid = punchtypeid;
        GregorianCalendar gc = new GregorianCalendar();
        this.originaltimestamp = gc.getTimeInMillis();
        this.adjustedtimestamp = gc.getTimeInMillis();
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
        return originaltimestamp;
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

    public void setOriginalTimeStamp(long originaltimestamp) {
        this.originaltimestamp = originaltimestamp;
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
    
    public String printOriginalTimestamp() {
        StringBuilder s = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MM/dd/yyyy HH:mm:ss");
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(originaltimestamp);
        s.append("#").append(badge.getId()); 
        s.append(" ");
        s.append(punchDescriptions[punchtypeid]);
        s.append(": ");
        s.append(sdf.format(gc.getTime()));
         
        return s.toString().toUpperCase();
    }
    
    public String printAdjustedTimestamp(){
        StringBuilder s = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MM/dd/yyyy HH:mm:ss");
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(adjustedtimestamp);
        s.append("#").append(badge.getId()); 
        s.append(" ");
        s.append(punchDescriptions[punchtypeid]);
        s.append(": ");
        s.append(sdf.format(gc.getTime()));
         
        return s.toString().toUpperCase();
    }
    
    
    //Feature 3
    public void adjust(Shift s){
        Shift adjShift = s; // sets shift to a local shift object
        
        /*V brings shift objects to local objects for easier usage V*/
        int interval = adjShift.getInterval();
        int grace = adjShift.getGracePeriod();
        int dock = adjShift.getDock();
        int counter = 0;
        GregorianCalendar originalTimeStamp = new GregorianCalendar();
        originalTimeStamp.setTimeInMillis(originaltimestamp);
        GregorianCalendar adjustedTimeStamp = new GregorianCalendar();
        adjustedTimeStamp.setTimeInMillis(originaltimestamp);
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
        
        adjustedTimeStamp.set(Calendar.SECOND, 0);
        
        
        /*V If statement nest to determine where a stamp should be adjusted and how. V*/
        if(punchtypeid == 1){
            if(0<=adjustedTimeStamp.compareTo(timeCheck.get(0)) && adjustedTimeStamp.compareTo(timeCheck.get(1))<=0)         // before clock-in
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(1).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(1).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(1).get(Calendar.SECOND));
            }
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(1)) && adjustedTimeStamp.compareTo(timeCheck.get(2))<=0)    // after clock-in, but w/ grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(1).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(1).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(1).get(Calendar.SECOND));
            }
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(2)) && adjustedTimeStamp.compareTo(timeCheck.get(3))<=0)    // after clock-in w/o grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(3).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(3).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(3).getTime())));
            }
            
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(4)) && adjustedTimeStamp.compareTo(timeCheck.get(5))<=0)    // Lunch Check clock in
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(5).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(5).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(5).getTime())));
            }
            
            
            else   // auto interval adjustment
            {
                int originalMinute = originalTimeStamp.get(Calendar.MINUTE);
                //interval value is stored in "shiftinterval"
                int adjustedMinute = 0;
                if(originalMinute % interval !=0){
                    if((originalMinute % interval) < (interval/2)){
                        adjustedMinute = (Math.round(originalMinute/interval)*interval);
                    }
                    else{
                        adjustedMinute = (Math.round(originalMinute/interval)*interval) + interval;
                    }
                    adjustedTimeStamp.add(Calendar.MINUTE, (adjustedMinute - originalMinute));
                    adjustedTimeStamp.set(Calendar.SECOND, 0);
                }
            }
            
            
        }
        
        else if(punchtypeid == 0){
            
            if(0<=adjustedTimeStamp.compareTo(timeCheck.get(4)) && adjustedTimeStamp.compareTo(timeCheck.get(5))<=0)    // Lunch Check clock out
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(4).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(4).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(4).getTime())));
            }
    
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(6)) && adjustedTimeStamp.compareTo(timeCheck.get(7))<=0)    // Clock-out w/o grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(6).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(6).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(6).getTime())));
            }
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(7)) && adjustedTimeStamp.compareTo(timeCheck.get(8))<=0)    // clock out w/ grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(8).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(8).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(8).getTime())));
            }
            else if(0<=adjustedTimeStamp.compareTo(timeCheck.get(8)) && adjustedTimeStamp.compareTo(timeCheck.get(9))<=0)    // clock-out late
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, Integer.parseInt((new SimpleDateFormat("HH")).format(timeCheck.get(8).getTime())));
                adjustedTimeStamp.set(Calendar.MINUTE, Integer.parseInt((new SimpleDateFormat("mm")).format(timeCheck.get(8).getTime())));
                adjustedTimeStamp.set(Calendar.SECOND, Integer.parseInt((new SimpleDateFormat("ss")).format(timeCheck.get(8).getTime())));
            }
            
            else   // interval check
            {
                int originalMinute = originalTimeStamp.get(Calendar.MINUTE);
                //interval value is stored in "shiftinterval"
                int adjustedMinute = 0;
                if(originalMinute % interval !=0){
                    if((originalMinute % interval) < (interval/2)){
                        adjustedMinute = (Math.round(originalMinute/interval)*interval);
                    }
                    else{
                        adjustedMinute = (Math.round(originalMinute/interval)*interval) + interval;
                    }
                    adjustedTimeStamp.add(Calendar.MINUTE, (adjustedMinute - originalMinute));
                    adjustedTimeStamp.set(Calendar.SECOND, 0);
                }
            }
        }
        adjustedtimestamp = adjustedTimeStamp.getTimeInMillis();
    }
}

