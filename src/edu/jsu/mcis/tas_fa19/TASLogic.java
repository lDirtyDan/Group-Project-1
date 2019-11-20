package edu.jsu.mcis.tas_fa19;

import java.util.ArrayList;
import org.json.simple.*;
import java.util.HashMap;

public class TASLogic {
    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){
        punchManagment(dailypunchlist);
        
        return 0; //replace later
    }
    
    public static void punchManagment(ArrayList<Punch> dailypunchlist){
        for(int list=0; list>0; list++){
        
        }
    }
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist){
         
         
         // Feature 5
         String json = " ";
         
         ArrayList<HashMap<String, String>> jsonData = new ArrayList();
         for (Punch punch : dailypunchlist){
         HashMap<String, String> punchData = new HashMap<>();
         punchData.put("ID", String.valueOf(punch.getId()));
         punchData.put("Badgeid", String.valueOf(punch.getBadgeid()));
         punchData.put("Badgetypieid", String.valueOf(punch.getPunchtypeid()));
         punchData.put("TerminalID", String.valueOf(punch.getTerminalid()));
         punchData.put("Punchtypieid", String.valueOf(punch.getPunchtypeid()));
         punchData.put("Punchdata", String.valueOf(punch.getPunchDescriptions()));
         }                  
         return json;
     }
}
