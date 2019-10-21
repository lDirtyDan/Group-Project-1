package edu.jsu.mcis.tas_fa19;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

public class TASDatabase {
    
    private String query, key, value;
    private String server = null;
    private String username = null;
    private String password = null;
    private Connection conn = null;
    private PreparedStatement pstSelect = null;
    private ResultSet resultset = null;
    private ResultSetMetaData metadata = null;
    private Punch punchDB = null;
    private Badge badgeDB = null;
    private Shift shiftDB = null;
    private boolean hasresults;
    
    public TASDatabase(){
        server = ("jdbc:mysql://localhost/TAS_FA19");
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
    
    /*This returns relevant objects to null so there is no conflicts.*/
    private void NullifyRelevant(){
    
        query = null;
        key = null;
        value = null;
        pstSelect = null;
        resultset = null;
        metadata = null;
        punchDB = null;
        badgeDB = null;
        shiftDB = null;
        hasresults = false;
        
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
        LocalTime shiftStart;
        LocalTime shiftStop;
        int interval;
        int gracePeriod;
        int dock;
        LocalTime lunchStart;
        LocalTime lunchStop;
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
                        metadata = resultset.getMetaData();
                        
                        while(resultset.next()){
                            shiftID = resultset.getInt("id");
                            shiftDesc = resultset.getString("description");
                            shiftStart = longToLocalDateTime(resultset.getLong("start")).toLocalTime();
                            shiftStop = longToLocalDateTime(resultset.getLong("start")).toLocalTime();
                            interval = resultset.getInt("interval");
                            gracePeriod = resultset.getInt("gracePeriod");
                            dock = resultset.getInt("dock");
                            lunchStart = longToLocalDateTime(resultset.getLong("lunchstart")).toLocalTime();
                            lunchStop = longToLocalDateTime(resultset.getLong("lunchstop")).toLocalTime();
                            lunchDeduct = resultset.getInt("lunchdeduct");
                            
                            shiftDB = new Shift(shiftID,shiftDesc,shiftStart,shiftStop,interval,gracePeriod,dock,lunchStart,lunchStop,lunchDeduct);
                        }
                    }
                }
                
                
            }
            Close();
        }
        
        catch (Exception e){
            return shiftDB;
        }
        
        return shiftDB;
    }
    
     public Shift getShift(Badge badgeLocal){
            
        shiftDB = null;
        
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
                        metadata = resultset.getMetaData();
   
                        while(resultset.next()){
                            shiftID = resultset.getInt("shiftid");
                        
                            shiftDB = getShift(shiftID);
                        }
                        
                    }
                }
                
                
            }
            Close();
        }
        
        catch (Exception e){
            return shiftDB;
        }
        
        return shiftDB;
       
     }
    
    /*Grabs the Punch data*/
    public Punch getPunch(int id){
        
        NullifyRelevant();
        
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
                        metadata = resultset.getMetaData();
   
                        while(resultset.next()){
                            badgeID = resultset.getString("badgeid");
                            
                            getBadge(badgeID);
                            
                            timeStamp = resultset.getLong("longtimestamp");
                            termID = resultset.getInt("terminalid");
                            punchType = resultset.getInt("punchtypeid");
                            
                            punchDB = new Punch(badgeDB, termID, punchType);
                                
                            
                            punchDB.setOriginalTimeStamp(longToLocalDateTime(timeStamp));
                            
                        }
                        
                    }
                }
                
                
            }
            Close();
        }
        
        catch (Exception e){
            return punchDB;
        }
        
        return punchDB;
    }
    
    /*Grabs the Badge data*/
    public Badge getBadge(String id){
        
        NullifyRelevant();
        
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
                        metadata = resultset.getMetaData();
   
                        while(resultset.next()){
                            badgeID = resultset.getString("id");
                            badgeDesc = resultset.getString("description");
                        
                            badgeDB = new Badge(badgeID, badgeDesc);
                        }
                        
                    }
                }
                
                
            }
            Close();
        }
        
        catch (Exception e){
            return badgeDB;
        }
  
        return badgeDB;
    }
    
}