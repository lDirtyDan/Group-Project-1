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
import java.util.logging.Level;

public class TASDatabase {
    
    
    /*Change values to local function*/
    
    private Connection conn = null;
    
    public TASDatabase(){
        String RETURN_GENERATED_KEYS = null;
        String server = ("jdbc:mysql://localhost/tas");
        String username = "A";
        String password = "abc123";
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, password);
        } catch (Exception e) {
            System.out.println("Connection Failed");
        }
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
        
        Shift shiftDB = null;
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
        
        String query = null;
        ResultSet resultset = null;
        PreparedStatement pstSelect = null;
        boolean hasresults;
        
        try {
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT *, UNIX_TIMESTAMP(start) * 1000 AS starttime, "
                        + "UNIX_TIMESTAMP(stop) * 1000 AS stoptime, "
                        + "UNIX_TIMESTAMP(lunchstart) * 1000 AS lunchstarttime, "
                        + "UNIX_TIMESTAMP(lunchstop) * 1000 AS lunchstoptime "
                        + "FROM shift WHERE id = ?";
                pstSelect = conn.prepareStatement(query);
                pstSelect.setInt(1, id);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
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
            }
        }
        
        catch (Exception e){
            return shiftDB;
        }
        
        return shiftDB;
    }
    
     public Shift getShift(Badge badgeLocal){
        
        int shiftID;
        String badgeID = null;
        Shift shiftDB = null;
        
        badgeID = badgeLocal.getId();
        
        String query = null;
        ResultSet resultset = null;
        PreparedStatement pstSelect = null;
        boolean hasresults;
        
        try {
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT * FROM employee WHERE badgeid = ?";
                pstSelect = conn.prepareStatement(query);
                pstSelect.setString(1, badgeID);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
                if (hasresults) {
                    resultset = pstSelect.getResultSet();
   
                    while(resultset.next()){
                        shiftID = resultset.getInt("shiftid");
                       
                        shiftDB = getShift(shiftID);
                    }        
                }
            }
                
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
        Punch punchDB = null;
        
        String query = null;
        ResultSet resultset = null;
        PreparedStatement pstSelect = null;
        boolean hasresults;
        
        try {
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT *, UNIX_TIMESTAMP(ORIGINALTIMESTAMP) * 1000 AS longtimestamp FROM punch WHERE id = ? ORDER BY originaltimestamp";
                pstSelect = conn.prepareStatement(query);
                pstSelect.setInt(1, id);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
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
            }
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
        Badge badgeDB = null;
        
        String query = null;
        ResultSet resultset = null;
        PreparedStatement pstSelect = null;
        boolean hasresults;
        
        try {
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified id*/
                query = "SELECT * FROM badge WHERE id = ?";
                pstSelect = conn.prepareStatement(query);
                pstSelect.setString(1, id);
                hasresults = pstSelect.execute();
                
                /*Gathers the specified data from the MySQL Database*/
                if (hasresults) {
                    resultset = pstSelect.getResultSet();
   
                    while(resultset.next()){
                        badgeID = resultset.getString("id");
                        badgeDesc = resultset.getString("description");
                        
                        badgeDB = new Badge(badgeID, badgeDesc);
                    }  
                }
            }
        }
        
        catch (Exception e){
            return badgeDB;
        }
  
        return badgeDB;
    }
    
    
    
    //Feature 2
 
    public int insertPunch(Punch punch){
        //use a getter method to extract a punch data from a object
        //insert that data into the database as a new punch
        //should return ID of the new punch as an Integar
        

        Punch p = punch;
        String badgeID = p.getBadgeid();
        int terminalid = p.getTerminalid();
        int punchtypeid = p.getPunchtypeid();
        int id = 0;
    
        String query = null;
        ResultSet resultset = null;
        PreparedStatement pstUpdate = null, pstSelect = null;
        boolean hasresults;
        int updateCount;
    
        try{
            query = "INSERT INTO punch(badgeid, terminalid, punchtypeid) VALUES (?, ?, ?)";
            pstUpdate = conn.prepareStatement(query, punchtypeid);
            pstUpdate.setString(1, badgeID);
            pstUpdate.setInt(2, terminalid);
            pstUpdate.setInt(3, punchtypeid);
            updateCount = pstUpdate.executeUpdate();

            // Get New Key; Print To Console
            if (updateCount > 0) {
                resultset = pstUpdate.getGeneratedKeys();
                if (resultset.next()) {
                    id = resultset.getInt(1);
                    
                    p.setId(id);
                }

            }

        }
    
        catch (Exception e) {
            //System.err.println("Unable to connect to the database");
            System.err.println(e.toString());
        }
        
        return id;
    
    }
    
    
    
    public ArrayList<Punch> getDailyPunchList (Badge badge, long ts){
        //should retrieve a list of all punches entered under the given badge within the day in which the timestamp occurred
        //The punches should be added to the list as individual Punch objects
        String badgeID = badge.getId();
        ArrayList<Punch> list = new ArrayList();
        GregorianCalendar origTimeStamp = new GregorianCalendar();
        origTimeStamp.setTimeInMillis(ts);
        String originaltimestamp = (new SimpleDateFormat("yyyy-MM-dd ")).format(origTimeStamp.getTime());
        String startTime = originaltimestamp + "00:00:00";
        String stopTime = originaltimestamp + "23:59:59";
        
        String query = null;
        PreparedStatement pstSelect = null;
        boolean hasresults;
        
        
        try {
            if (conn.isValid(0)) {
                /*Command sent to MySQL Database to search for specified badgeid and timestamp*/
                query = "SELECT *, UNIX_TIMESTAMP(ORIGINALTIMESTAMP) * 1000 AS longtimestamp FROM punch WHERE badgeid = ? AND originaltimestamp BETWEEN ? AND ? ORDER BY originaltimestamp";
                pstSelect = conn.prepareStatement(query);
                pstSelect.setString(1, badgeID);
                pstSelect.setString(2,startTime);
                pstSelect.setString(3,stopTime);
                hasresults = pstSelect.execute();
                /*Gathers the specified data from the MySQL Database and adds it to an array*/
                if (hasresults == true) {
                    ResultSet resultset = pstSelect.getResultSet();
   
                    while(resultset.next()){
                        int id = resultset.getInt("id");
                            
                        list.add(getPunch(id));
                    }
                }
            }
        }
        
        catch (Exception e){
            e.printStackTrace();
            return list;
        }
        return list;
    }
}
    
