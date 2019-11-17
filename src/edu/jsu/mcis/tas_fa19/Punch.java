package edu.jsu.mcis.tas_fa19;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
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
        s.append(sdf.format(gc.getTime()).toUpperCase());
        s.append(" ");
        s.append(adjustmenttype);
         
        return s.toString();
    }
    
    public HashMap<String,Long> createLongHashMap(String[] timeType,ArrayList<LocalTime> timeClock, Shift adjShift){
        int interval = adjShift.getInterval();
        int grace = adjShift.getGracePeriod();
        int dock = adjShift.getDock();
        String[] timeExt = new String[]{"startEarly","startGrace","startLate","stopEarly","stopGrace","stopLate"};
        HashMap<String,Long> baseComp = new HashMap<String,Long>();
        for(int counter = 0; counter < 4; counter++){
            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal.setTimeInMillis(originaltimestamp);
            
            tempCal.set(Calendar.HOUR_OF_DAY, timeClock.get(counter).getHour());
            tempCal.set(Calendar.MINUTE, timeClock.get(counter).getMinute());
            tempCal.set(Calendar.SECOND, 0);
            
            baseComp.put(timeType[counter], tempCal.getTimeInMillis());
        }
        for(int counter = 0; counter < 3; counter++){
            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal.setTimeInMillis(baseComp.get("start"));
            
            switch(counter){
                case 0: tempCal.add(Calendar.MINUTE, -dock);
                        break;
                case 1: tempCal.add(Calendar.MINUTE, grace);
                        break;
                case 2: tempCal.add(Calendar.MINUTE, interval);
                        break;
            }
            
            baseComp.put(timeExt[counter], tempCal.getTimeInMillis());
        }
        for(int counter = 0; counter < 3; counter++){
            GregorianCalendar tempCal = new GregorianCalendar();
            tempCal.setTimeInMillis(baseComp.get("stop"));
            int counterMod = counter + 3;
            
            switch(counter){
                case 0: tempCal.add(Calendar.MINUTE, -dock);
                        break;
                case 1: tempCal.add(Calendar.MINUTE, -grace);
                        break;
                case 2: tempCal.add(Calendar.MINUTE, interval);
                        break;
            }
            
            baseComp.put(timeExt[counterMod], tempCal.getTimeInMillis());
        }
        
        return baseComp;
    }
    
    public void intervalAdjust(Shift adjShift,String[] adjType){
        int interval = adjShift.getInterval();
        GregorianCalendar originalTimeStamp = new GregorianCalendar();
        originalTimeStamp.setTimeInMillis(originaltimestamp);
        GregorianCalendar adjustedTimeStamp = new GregorianCalendar();
        adjustedTimeStamp.setTimeInMillis(originaltimestamp);
        
        int originalMinute = originalTimeStamp.get(Calendar.MINUTE);
        //interval value is stored in "shiftinterval"
        int adjustedMinute = 0;
        if(originalMinute % interval !=0){
            if((originalMinute % interval) < (interval/2)){
                adjustedMinute = (Math.round(originalMinute/interval)*interval);
                adjustmenttype = adjType[5];
            }
            else{
                adjustedMinute = (Math.round(originalMinute/interval)*interval) + interval;
                adjustmenttype = adjType[5];
            }
            adjustedTimeStamp.add(Calendar.MINUTE, (adjustedMinute - originalMinute));
        }
        else{adjustmenttype = adjType[6];}
        adjustedTimeStamp.set(Calendar.SECOND, 0);
        adjustedtimestamp = adjustedTimeStamp.getTimeInMillis();
    }
    
    //Feature 3
    public void adjust(Shift s){
         // sets shift to a local shift object
        Shift adjShift = s;
         
        /*V organization of objects for easier use V*/
        GregorianCalendar originalTimeStamp = new GregorianCalendar();
        originalTimeStamp.setTimeInMillis(originaltimestamp);
        SimpleDateFormat weekDay = new SimpleDateFormat("EEE");
        String day = weekDay.format(originalTimeStamp.getTime()).toUpperCase();
        
        HashMap<String,Long> timeCheck = new HashMap<String,Long>();
        ArrayList<LocalTime> timeClock = new ArrayList<LocalTime>();
        String[] timeType = new String[]{"start","lunchStart","lunchStop","stop"};
        String[] adjType = new String[]{"(Shift Start)","(Shift Stop)","(Shift Dock)","(Lunch Start)","(Lunch Stop)","(Interval Round)","(None)"};
        
        timeClock.add(adjShift.getStart());                           
        timeClock.add(adjShift.getLunchStart());                     
        timeClock.add(adjShift.getLunchStop());                       
        timeClock.add(adjShift.getStop());                            
        
        timeCheck = createLongHashMap(timeType,timeClock,adjShift); // Creates HashMap with keys corresponding to specific timestamps in milliseconds (long)
        
        /*V If statement nest to determine where a stamp should be adjusted and how. V*/
        if(!day.equals("SAT") && !day.equals("SUN")){
            if(punchtypeid == CLOCK_IN){
                if(originaltimestamp >= timeCheck.get("startEarly") && timeCheck.get("startGrace") >= originaltimestamp){adjustedtimestamp = timeCheck.get("start"); adjustmenttype = adjType[0];}
                else if(originaltimestamp >= timeCheck.get("startGrace") && timeCheck.get("startLate") >= originaltimestamp){adjustedtimestamp = timeCheck.get("startLate"); adjustmenttype = adjType[2];}
                else if(originaltimestamp >= timeCheck.get("lunchStart") && timeCheck.get("lunchStop") >= originaltimestamp){adjustedtimestamp = timeCheck.get("lunchStop"); adjustmenttype = adjType[4];}
                else {intervalAdjust(adjShift,adjType);}
            }
        
            else if(punchtypeid == CLOCK_OUT){
            
                if(originaltimestamp >= timeCheck.get("lunchStart") && timeCheck.get("lunchStop") >= originaltimestamp){adjustedtimestamp = timeCheck.get("lunchStart"); adjustmenttype = adjType[3];}
                else if(originaltimestamp >= timeCheck.get("stopEarly") && timeCheck.get("stopGrace") >= originaltimestamp){adjustedtimestamp = timeCheck.get("stopEarly"); adjustmenttype = adjType[2];}
                else if(originaltimestamp >= timeCheck.get("stopGrace") && timeCheck.get("stopLate") >= originaltimestamp){adjustedtimestamp = timeCheck.get("stop"); adjustmenttype = adjType[1];}
                else {intervalAdjust(adjShift,adjType);}
            }
        }
        else{intervalAdjust(adjShift,adjType);}
        timeCheck.clear();
    }
}

