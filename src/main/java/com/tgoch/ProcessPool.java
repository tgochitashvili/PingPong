package com.tgoch;

import java.util.LinkedList;

import com.json.JSONArray;
import com.json.JSONObject;
public class ProcessPool{
    private final LinkedList<Process> processList;
    public String serverName;
   

    public LinkedList<Process> getProcessList(){
        return this.processList;
    }

    public void addProcess(Process process){
        processList.add(process);
    }
    public LinkedList<Process> mismatchedProcesses(){
        LinkedList<Process> tempProcessList = new LinkedList<>();
        for(Process process: this.processList){
            if(!process.checkResponse()){
                tempProcessList.add(process);
            }
        }
        return tempProcessList;
    }

    public JSONObject toJSON(){
        JSONObject root = new JSONObject();
        JSONArray successfulArray = new JSONArray();
        JSONArray unsuccessfulArray = new JSONArray();

        for(Process process: processList){
            if(process.checkResponse()) {
                successfulArray.put(process.toJSON());
            }
            else {
                unsuccessfulArray.put(process.toJSON());
            }
        }
        root.put("serverName", serverName);
        root.put("unsuccessfulProcesses", unsuccessfulArray);
        root.put("successfulProcesses", successfulArray);
        return root;
    }


    public ProcessPool(LinkedList<Process> processList, String serverName){
        this.processList = new LinkedList<>(processList);
        this.serverName = serverName;
    }
    public ProcessPool(LinkedList<Process> processList){
        this.processList = new LinkedList<>(processList);
        this.serverName = "";
    }
    public ProcessPool(String serverName){
        this.processList = new LinkedList<>();
        this.serverName = serverName;
    }
    public ProcessPool(){
        this.processList = new LinkedList<>();
        this.serverName = "";
    }
}