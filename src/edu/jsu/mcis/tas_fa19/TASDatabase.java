package edu.jsu.mcis.tas_fa19;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.LocalTime;
import java.util.TimeZone;
//Feature 2 Imports
import java.util.GregorianCalendar;
import java.util.Calendar;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.text.SimpleDateFormat;

public class TASDatabase {
    
    private String query = null;
    private String server = null;
    private String username = null;
    private String password = null;
    private Connection conn = null;
    private PreparedStatement pstSelect = null;
    private ResultSet resultset = null;
    private Punch punchDB = null;
    private Badge badgeDB = null;
    private Shift shiftDB = null;
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
    
    private LocalTime longToLocalTime(long longTime){
        
        LocalTime timeStamp = Instant.ofEpochMilli(longTime).atZone(ZoneId.systemDefault()).toLocalTime();
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
                query = "SELECT * FROM shift WHERE id = '"+ id +"'";
                pstSelect = conn.prepareStatement(query);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
                while (hasresults || pstSelect.getUpdateCount() != -1) {
                    if (hasresults) {
                        resultset = pstSelect.getResultSet();
                        
                        while(resultset.next()){
                            shiftID = resultset.getInt("id");
                            shiftDesc = resultset.getString("description");
                            shiftStart = longToLocalTime(resultset.getLong("start"));
                            shiftStop = longToLocalTime(resultset.getLong("stop"));
                            interval = resultset.getInt("interval");
                            gracePeriod = resultset.getInt("gracePeriod");
                            dock = resultset.getInt("dock");
                            lunchStart = longToLocalTime(resultset.getLong("lunchstart"));
                            lunchStop = longToLocalTime(resultset.getLong("lunchstop"));
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
                                
                            
                            punchDB.setOriginalTimeStamp(longToLocalDateTime(timeStamp));
                            
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
        
        
    }
    
    public ArrayList<Punch> getDailyPunchList (Badge badge, long ts){
        //should retrieve a list of all punches entered under the given badge within the day in which the timestamp occurred
        //The punches should be added to the list as individual Punch objects
        
    }
    
}
