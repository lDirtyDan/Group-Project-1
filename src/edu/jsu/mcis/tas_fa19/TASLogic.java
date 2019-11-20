package edu.jsu.mcis.tas_fa19;

import java.util.ArrayList;
import org.json.simple.*;
import java.util.HashMap;

public class TASLogic {
    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){
        int totalMinutes = 0;
        boolean inPair = false;
        
        for(Punch punch : dailypunchlist){
            int counter = 0;
            int punchType = dailypunchlist.get(counter).getPunchtypeid();
            while(punchType != 3){
            
            }
            counter++;
        }
        
        return totalMinutes;
    }
    public static String getPunchListAsJSON(ArrayList<Punch> dailypunchlist){
         
         
         // Feature 5
         String json = " ";
         
         ArrayList<HashMap<String, String>> jsonData = new ArrayList();
         for (Punch punch : dailypunchlist){
         HashMap<String, String> punchData = new HashMap<>();
         punchData.put("id", String.valueOf(punch.getId()));
         punchData.put("badgeid", String.valueOf(punch.getBadgeid()));
         }                  
         return json;
     }
}
