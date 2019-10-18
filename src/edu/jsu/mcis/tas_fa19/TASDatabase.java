package edu.jsu.mcis.tas_fa19;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TASDatabase {
    
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
    
    public TASDatabase(){
        server = ("jdbc:mysql://localhost/TAS_FA19");
        username = "A";
        password = "abc123";
    }
    // V delete this later V
    public JSONArray getJSONData(){
        
        String[] headerName = {"badge", "shift"};
        
        JSONObject jsonFile = new JSONObject();
        JSONArray headData = new JSONArray();
        JSONArray finArray = new JSONArray();
        
        String query, key, value;
        boolean hasresults;
        int resultCount, columnCount, updateCount = 0;
        
        try {
            Open();
            if (conn.isValid(0)) {
                for(int num = 0; num > 3; num++){
                    query = "SELECT * FROM " + headerName[num];
                    pstSelect = conn.prepareStatement(query);
                    hasresults = pstSelect.execute();
                
                    /*Grabs and Formats information */
                    while ( hasresults || pstSelect.getUpdateCount() != -1 ) {
                        
                        /*Reads in data from specified area of the database above*/
                        if ( hasresults ) {
                            resultset = pstSelect.getResultSet();
                            metadata = resultset.getMetaData();
                            columnCount = metadata.getColumnCount();
                            
                            for (int i = 1; i <= columnCount; i++) {
                                key = metadata.getColumnLabel(i);
                                headData.add(key);
                            }
                        
                            while(resultset.next()) { 
                                for (int i = 1; i <= columnCount; i++) {
                                    value = resultset.getString(i);
                                    if (!resultset.wasNull()) {
                                        if(i != 1){
                                            jsonFile.put(headData.get(i-1),value);
                                        }
                                    }
                                }
                                finArray.add(jsonFile.clone());
                                jsonFile.clear();
                            }
                            
                        }
                    
                        else {
                            resultCount = pstSelect.getUpdateCount();  
                            if ( resultCount == -1 ) {
                                break;
                            }
                        }
                        hasresults = pstSelect.getMoreResults();
                        
                    }
                }
            }   
            Close();
        }
        catch(Exception e) { return finArray; }   
        return finArray;
    }   
    /*Opens connection to MySQL Server*/
    public void Open(){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, password);
        }
        catch(Exception e) { System.out.println("Connection Failed."); } 
        System.out.println("Connection Success!");
    }
    
    /*Closes connection to MySQL Server*/
    public void Close(){
        try{
            conn.close();
        }
        catch(Exception e) { System.out.println("Connection could not be closed."); } 
        System.out.println("Connection closed.");
    }
    
    /*Grabs the Shift data*/
    public Shift getShift(){
        Close();
        try {
            Open();
            
            Close();
        }
        
        catch (Exception e){
            return null;
        }
        
        return shiftDB;
    }
    
    /*Grabs the Punch data*/
    public Punch getPunch(){
        Close();
        try {
            Open();
            
            Close();
        }
        
        catch (Exception e){
            return null;
        }
        
        return punchDB;
    }
    
    /*Grabs the Badge data*/
    public Badge getBadge(){
        Close();
        try {
            Open();
            
            Close();
        }
        
        catch (Exception e){
            return null;
        }
  
        return badgeDB;
    }
    
}