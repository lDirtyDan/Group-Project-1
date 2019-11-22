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
                if(!inPair && punchType == 1){
                }
                if(inPair && punchType == 0){
                }
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
            punchData.put("ID", String.valueOf(punch.getId()));
            punchData.put("Badgeid", String.valueOf(punch.getBadgeid()));
            punchData.put("Badgetypieid", String.valueOf(punch.getPunchtypeid()));
            punchData.put("TerminalID", String.valueOf(punch.getTerminalid()));
            punchData.put("Punchtypieid", String.valueOf(punch.getPunchtypeid()));
            punchData.put("Punchdata", String.valueOf(punch.getPunchDescriptions()));
            punchData.put("originaltimestamp", String.valueOf(punch.getOriginaltimestamp()));
            punchData.put("adjustedtimestamp", String.valueOf(punch.getAdjustedtimestamp()));
            jsonData.add(punchData);
         }                  
         json = JSONValue.toJSONString(jsonData);
         return json;
     }
}
