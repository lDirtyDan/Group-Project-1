package edu.jsu.mcis.tas_fa19;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalTime;
import java.util.TimeZone;
//Feature 2 Imports
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

public class TASDatabase {
    
    private String query = null;
    private String server = null;
    private String username = null;
    private String password = null;
    private Connection conn = null;
    private PreparedStatement pstSelect = null, pstUpdate = null;
    private ResultSet resultset = null;
    private Punch punchDB = null;
    private Badge badgeDB = null;
    private Shift shiftDB = null;
    private String RETURN_GENERATED_KEYS = null;
    int updateCount = 0;
    private boolean hasresults;
    
    public TASDatabase(){
        server = ("jdbc:mysql://localhost/tas");
        username = "A";
        password = "abc123";
    }
    
    /*Closes connection to MySQL Server*/
    public void Close(){
        try{
            conn.close();
        }
        catch(Exception e) { System.out.println("Connection could not be closed."); } 
        System.out.println("Connection closed.");
    }
    
    private LocalDateTime longToLocalDateTime(long longTime){
        
        LocalDateTime timeStamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(longTime), TimeZone.getDefault().toZoneId());
        return timeStamp;
    
    }
    
    /*Grabs the Shift data*/
    
    public Shift getShift(int id){
        
        shiftDB = null;
        
        int shiftID;
        String shiftDesc = null;
        LocalTime shiftStart = null;
        LocalTime shiftStop = null;
        int interval;
        int gracePeriod;
        int dock;
        LocalTime lunchStart = null;
        LocalTime lunchStop = null;
        int lunchDeduct;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, password);
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT *, UNIX_TIMESTAMP(start) * 1000 AS starttime, "
                        + "UNIX_TIMESTAMP(stop) * 1000 AS stoptime, "
                        + "UNIX_TIMESTAMP(lunchstart) * 1000 AS lunchstarttime, "
                        + "UNIX_TIMESTAMP(lunchstop) * 1000 AS lunchstoptime "
                        + "FROM shift WHERE id = '"+ id +"'";
                pstSelect = conn.prepareStatement(query);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
                while (hasresults || pstSelect.getUpdateCount() != -1) {
                    if (hasresults) {
                        resultset = pstSelect.getResultSet();
                        
                        while(resultset.next()){
                            shiftID = resultset.getInt("id");
                            shiftDesc = resultset.getString("description");
                            shiftStart = longToLocalDateTime(resultset.getLong("starttime")).toLocalTime();
                            shiftStop = longToLocalDateTime(resultset.getLong("stoptime")).toLocalTime();
                            interval = resultset.getInt("interval");
                            gracePeriod = resultset.getInt("gracePeriod");
                            dock = resultset.getInt("dock");
                            lunchStart =longToLocalDateTime(resultset.getLong("lunchstarttime")).toLocalTime();
                            lunchStop = longToLocalDateTime(resultset.getLong("lunchstoptime")).toLocalTime();
                            lunchDeduct = resultset.getInt("lunchdeduct");
                            
                            shiftDB = new Shift(shiftID,shiftDesc,shiftStart,shiftStop,interval,gracePeriod,dock,lunchStart,lunchStop,lunchDeduct);
                        }
                    }
                    
                    hasresults = pstSelect.getMoreResults();
                }
            }
            conn.close();
        }
        
        catch (Exception e){
            return shiftDB;
        }
        
        return shiftDB;
    }
    
     public Shift getShift(Badge badgeLocal){
        
        int shiftID;
        String badgeID = null;
        
        badgeID = badgeLocal.getId();
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, password);
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT * FROM employee WHERE badgeid = '"+ badgeID +"'";
                pstSelect = conn.prepareStatement(query);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
                while (hasresults || pstSelect.getUpdateCount() != -1) {
                    if (hasresults) {
                        resultset = pstSelect.getResultSet();
   
                        while(resultset.next()){
                            shiftID = resultset.getInt("shiftid");
                        
                            shiftDB = getShift(shiftID);
                        }
                        
                    }
                }
                
                hasresults = pstSelect.getMoreResults();
            }
            conn.close();
        }
        
        catch (Exception e){
            return shiftDB;
        }
        
        return shiftDB;
       
     }
    
    /*Grabs the Punch data*/
    public Punch getPunch(int id){
        
        String badgeID = null;
        long timeStamp;
        int punchType;
        int termID;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, password);
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT *, UNIX_TIMESTAMP(ORIGINALTIMESTAMP) * 1000 AS longtimestamp FROM punch WHERE id = '"+id+"'";
                pstSelect = conn.prepareStatement(query);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
                while (hasresults || pstSelect.getUpdateCount() != -1) {
                    if (hasresults) {
                        resultset = pstSelect.getResultSet();
   
                        while(resultset.next()){
                            badgeID = resultset.getString("badgeid");
                            
                            timeStamp = resultset.getLong("longtimestamp");
                            termID = resultset.getInt("terminalid");
                            punchType = resultset.getInt("punchtypeid");
                            
                            punchDB = new Punch(getBadge(badgeID), termID, punchType);
                                
                            
                            punchDB.setOriginalTimeStamp(timeStamp);
                            
                        }
                        
                    }
                    
                    hasresults = pstSelect.getMoreResults();
                }
            }
            conn.close();
        }
        
        catch (Exception e){
            return punchDB;
        }
        
        return punchDB;
    }
    
    /*Grabs the Badge data*/
    public Badge getBadge(String id){
        
        String badgeID = null;
        String badgeDesc = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, password);
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT * FROM badge WHERE id = '"+id+"'";
                pstSelect = conn.prepareStatement(query);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
                while (hasresults || pstSelect.getUpdateCount() != -1) {
                    if (hasresults) {
                        resultset = pstSelect.getResultSet();
   
                        while(resultset.next()){
                            badgeID = resultset.getString("id");
                            badgeDesc = resultset.getString("description");
                        
                            badgeDB = new Badge(badgeID, badgeDesc);
                        }
                        
                    }
                    
                    hasresults = pstSelect.getMoreResults();
                }
            }
            conn.close();
        }
        
        catch (Exception e){
            return badgeDB;
        }
  
        return badgeDB;
    }
    
    
    
    //Feature 2
 
    public int insertPunch(Punch p){
        //use a getter method to extract a punch data from a object
        //insert that data into the database as a new punch
        //should return ID of the new punch as an Integar
        

        
    String badgeid = p.getBadgeid();
    int terminalid = p.getTerminalid();
    int punchtypeid = p.getPunchtypeid();
    int id = 0;
    
    try
    {
       
        query = "INSERT INTO punch(badgeid, terminalid, punchtypeid) VALUES ('" + badgeid + "', " + terminalid + ", " + punchtypeid + ")";
        pstUpdate = conn.prepareStatement(query, punchtypeid);
                    
                    // Execute Update Query

                    updateCount = pstUpdate.executeUpdate();

                    // Get New Key; Print To Console

                    if (updateCount > 0) {

                        resultset = pstUpdate.getGeneratedKeys();

                        if (resultset.next()) {
                            
                            id = resultset.getInt(1);
                            
                            p.setId(id);
                            
                            return id;

                        }

                    }

    }
    
    catch (Exception e) 
    {
            //System.err.println("Unable to connect to the database");
            System.err.println(e.toString());
    }
    
    return id;
    
}
    
    
    
    public ArrayList<Punch> getDailyPunchList (Badge badge, long ts){
        //should retrieve a list of all punches entered under the given badge within the day in which the timestamp occurred
        //The punches should be added to the list as individual Punch objects
        String badgeid = badge.getId();
        ArrayList<Punch> list = new ArrayList();
        GregorianCalendar origTimeStamp = new GregorianCalendar();
        origTimeStamp.setTimeInMillis(ts);
        String originaltimestamp = (new SimpleDateFormat("yyyy-MM-dd ")).format(origTimeStamp.getTime());
        String startTime = originaltimestamp;
        String stopTime = originaltimestamp;
        startTime = startTime.concat("00:00:00");
        stopTime = stopTime.concat("23:59:59");
        
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, password);
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT *, UNIX_TIMESTAMP(ORIGINALTIMESTAMP) * 1000 AS longtimestamp FROM punch WHERE badgeid = ? AND originaltimestamp BETWEEN ? AND ?";
                pstUpdate = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                pstUpdate.setString(1, badgeid);
                pstUpdate.setString(2,startTime);
                pstUpdate.setString(3,stopTime);
                pstSelect = conn.prepareStatement(query);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database and adds it to an array*/
                while (hasresults || pstSelect.getUpdateCount() != -1) {
                    if (hasresults) {
                        resultset = pstSelect.getResultSet();
   
                        while(resultset.next()){
                            int id = resultset.getInt("id");
                            
                            list.add(getPunch(id));
                        }
                        
                    }
                    
                    hasresults = pstSelect.getMoreResults();
                }
            }
            conn.close();
        }
        
        catch (Exception e){
            return list;
        }
    
        return list;
    }
}
    
