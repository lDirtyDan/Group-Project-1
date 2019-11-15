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
import java.util.HashMap;

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
    
    public HashMap<String,Long> createLongHashMap(String[] timeType,ArrayList<LocalTime> timeClock, Shift adjShift){
        int interval = adjShift.getInterval();
        int grace = adjShift.getGracePeriod();
        int dock = adjShift.getDock();
        int counter = 0;
        String[] timeExt = new String[]{"startEarly","startGrace","StartLate","stopEarly","stopGrace","stopLate"};
        HashMap<String,Long> baseComp = new HashMap<String,Long>();
        while(counter != 4){
            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal.setTimeInMillis(originaltimestamp);
            
            tempCal.set(Calendar.HOUR_OF_DAY, timeClock.get(counter).getHour());
            tempCal.set(Calendar.MINUTE, timeClock.get(counter).getMinute());
            tempCal.set(Calendar.SECOND, 0);
            
            baseComp.put(timeType[counter], tempCal.getTimeInMillis());
            counter++;
        }
        while(counter != 3){
            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal.setTimeInMillis(baseComp.get("start"));
            
            if(counter == 0){tempCal.roll(Calendar.MINUTE, -dock);}
            if(counter == 1){tempCal.roll(Calendar.MINUTE, grace);}
            if(counter == 2){tempCal.roll(Calendar.MINUTE, interval);}
            
            baseComp.put(timeType[counter], tempCal.getTimeInMillis());
            counter++;
        }
        counter = 0;
        while(counter != 3){
            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal.setTimeInMillis(baseComp.get("stop"));
            int counterMod = counter + 3;
            
            if(counter == 0){tempCal.roll(Calendar.MINUTE, -dock);}
            if(counter == 1){tempCal.roll(Calendar.MINUTE, -grace);}
            if(counter == 2){tempCal.roll(Calendar.MINUTE, interval);}
            
            
            
            baseComp.put(timeType[counterMod], tempCal.getTimeInMillis());
            counter++;
        }
        
        
        
        
        return baseComp;
    }
    
    //Feature 3
    public void adjust(Shift s){
         // sets shift to a local shift object
        
        /*V brings shift objects to local objects for easier usage V*/
        GregorianCalendar originalTimeStamp = new GregorianCalendar();
        originalTimeStamp.setTimeInMillis(originaltimestamp);
        GregorianCalendar adjustedTimeStamp = new GregorianCalendar();
        adjustedTimeStamp.setTimeInMillis(originaltimestamp);
        HashMap<String,Long> timeCheck = new HashMap<String,Long>();
        ArrayList<LocalTime> timeClock = new ArrayList<LocalTime>();
        String[] timeType = new String[]{"start","lunchStop","lunchStart","stop"};
        LocalTime start = adjShift.getStart();
        LocalTime stop = adjShift.getStop();
        LocalTime lunchStart = adjShift.getLunchStart();
        LocalTime lunchStop = adjShift.getLunchStop();
        
        timeClock.add(start);                           // ID = 0 Clock-in
        timeClock.add(lunchStart);                      // ID = 1 Lunch Starts
        timeClock.add(lunchStop);                       // ID = 2 Lunch Stops
        timeClock.add(stop);                            // ID = 3 Clock-out
        
        
        
        timeCheck = createLongHashMap(timeType,timeClock,s);
        
        adjustedTimeStamp.set(Calendar.SECOND, 0);
        
        
        /*V If statement nest to determine where a stamp should be adjusted and how. V*/
        if(punchtypeid == 1){
            if(0>=adjustedTimeStamp.compareTo(timeCheck.get(0)) && adjustedTimeStamp.compareTo(timeCheck.get(1))>=0)         // before clock-in
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(1).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(1).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(1).get(Calendar.SECOND));
                System.out.println("yes");
            }
            else if(0>=adjustedTimeStamp.compareTo(timeCheck.get(1)) && adjustedTimeStamp.compareTo(timeCheck.get(2))>=0)    // after clock-in, but w/ grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(1).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(1).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(1).get(Calendar.SECOND));
            }
            else if(0>=adjustedTimeStamp.compareTo(timeCheck.get(2)) && adjustedTimeStamp.compareTo(timeCheck.get(3))>=0)    // after clock-in w/o grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(3).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(3).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(3).get(Calendar.SECOND));
            }
            
            else if(0>=adjustedTimeStamp.compareTo(timeCheck.get(4)) && adjustedTimeStamp.compareTo(timeCheck.get(5))>=0)    // Lunch Check clock in
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(5).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(5).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(5).get(Calendar.SECOND));
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
            
            if(0>=adjustedTimeStamp.compareTo(timeCheck.get(4)) && adjustedTimeStamp.compareTo(timeCheck.get(5))>=0)    // Lunch Check clock out
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(4).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(4).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(4).get(Calendar.SECOND));
            }
    
            else if(0>=adjustedTimeStamp.compareTo(timeCheck.get(6)) && adjustedTimeStamp.compareTo(timeCheck.get(7))>=0)    // Clock-out w/o grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(6).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(6).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(6).get(Calendar.SECOND));
            }
            else if(0>=adjustedTimeStamp.compareTo(timeCheck.get(7)) && adjustedTimeStamp.compareTo(timeCheck.get(8))>=0)    // clock out w/ grace period
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(8).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(8).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(8).get(Calendar.SECOND));
            }
            else if(0>=adjustedTimeStamp.compareTo(timeCheck.get(8)) && adjustedTimeStamp.compareTo(timeCheck.get(9))>=0)    // clock-out late
            {
                adjustedTimeStamp.set(Calendar.HOUR_OF_DAY, timeCheck.get(8).get(Calendar.HOUR));
                adjustedTimeStamp.set(Calendar.MINUTE, timeCheck.get(8).get(Calendar.MINUTE));
                adjustedTimeStamp.set(Calendar.SECOND, timeCheck.get(8).get(Calendar.SECOND));
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