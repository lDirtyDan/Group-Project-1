package edu.jsu.mcis.tas_fa19;

import java.util.ArrayList;
import org.json.simple.*;
import java.util.HashMap;

public class TASLogic {
    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift){
        int totalMinutes = 0;
        int lunchDeduct = shift.getLunchDeduct();
        boolean inPair = false;
        boolean lunchCheck = false;
        long startTime = 0;
        long stopTime = 0;
        String adjType = null;
        
        for(Punch punch : dailypunchlist){
            int counter = 0;
            int punchType = dailypunchlist.get(counter).getPunchtypeid();
            if(punchType != 3){
                punch.adjust(shift);
                adjType = punch.getAdjustmenttype();
                if(adjType.equals("(Lunch Start)") || adjType.equals("(Lunch Stop)")){
                    lunchCheck = true;
                    System.out.println(totalMinutes);
                }
                
                if(!inPair && punchType == 1){
                    startTime = punch.getAdjustedtimestamp();
                    inPair = true;
                }
                if(inPair && punchType == 0){
                    stopTime = punch.getAdjustedtimestamp();
                    totalMinutes = totalMinutes + (int)((stopTime - startTime)*3600);
                    inPair = false;
                }
                
                if(lunchCheck && totalMinutes > 420){
                    totalMinutes = totalMinutes - lunchDeduct;
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
         }                  
         return json;
     }
}
