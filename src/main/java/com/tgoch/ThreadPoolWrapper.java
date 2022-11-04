package com.tgoch;
import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;
import com.json.JSONObject;

public class ThreadPoolWrapper{
    private String serverName;
    private ThreadPoolExecutor threadPoolExecutor;
    private ProcessPool processPool;

    public ThreadPoolWrapper setServerName(String serverName){
        this.serverName = serverName;
        return this;
    }
    public String getServerName(){
        return this.serverName;
    }
    public ThreadPoolWrapper setExecutor(ThreadPoolExecutor threadPoolExecutor){
        this.threadPoolExecutor = threadPoolExecutor;
        return this;
    }
    public ThreadPoolExecutor getExecutor(){
        return this.threadPoolExecutor;
    }
    public ThreadPoolWrapper setProcessPool(ProcessPool processPool){
        this.processPool = processPool;
        return this;
    }
    public ProcessPool getProcessPool(){
        return this.processPool;
    }
    public void runProcesses(boolean onlyErrors){
        LinkedList<Process> tempProcessList = this.processPool.mismatchedProcesses();
        if(onlyErrors){
            if(tempProcessList.size() > 0)
                runList(tempProcessList);
        }
        else{
            runList(this.processPool.getProcessList());
        }
    }
    public void runList(LinkedList<Process> processList){
        for(Process process: processList){
            this.threadPoolExecutor.submit(process);
        }
    }
    public JSONObject toJSON(){
        JSONObject root = new JSONObject();
        int numErrors = processPool.mismatchedProcesses().size();
        int processPoolSize = processPool.getProcessList().size();
        root.put("errors", numErrors);
        root.put("successes", processPoolSize - numErrors);
        root.put("processPool", processPool.toJSON()); 
        return root;
    }   
}
