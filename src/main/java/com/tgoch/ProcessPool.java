package com.tgoch;

import java.util.LinkedList;

import com.json.JSONArray;
import com.json.JSONObject;
public class ProcessPool{
    public LinkedList<Process> processList;
    public String serverName;
   

    public LinkedList<Process> mismatchedProcesses(String responseCode){
        LinkedList<Process> tempProcessList = new LinkedList<Process>();
        for(Process process: this.processList){
            if(!process.checkResponse(responseCode)){
                tempProcessList.add(process);
            }
        }
        return tempProcessList;
    }

    public JSONObject toJSON(){
        JSONObject root = new JSONObject();
        JSONArray array = new JSONArray();
        for(Process process: processList){
            array.put(process.toJSON());
        }
        root.put("serverName", serverName);
        root.put("processes", array);
        return root;
    }


    public ProcessPool(LinkedList<Process> processList, String serverName){
        this.processList = new LinkedList<Process>(processList);
        this.serverName = serverName;
    }
    public ProcessPool(LinkedList<Process> processList){
        this.processList = new LinkedList<Process>(processList);
        this.serverName = "";
    }
    public ProcessPool(String serverName){
        this.processList = new LinkedList<Process>();
        this.serverName = serverName;
    }
    public ProcessPool(){
        this.processList = new LinkedList<Process>();
        this.serverName = "";
    }
}