import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolWrapper{
    private String serverName;
    private ThreadPoolExecutor threadPoolExecutor;
    private ProcessPool processPool;
    private String successCode = "200";

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
    public ThreadPoolWrapper setSuccessCode(String successCode){
        this.successCode = successCode;
        return this;
    }
    public String getSuccessCode(){
        return this.successCode;
    }

    public ThreadPoolWrapper runProcesses(boolean onlyErrors){
        if(this.processPool.processList.size() < 0){
            return this;
        }
        LinkedList<Process> tempProcessList = this.processPool.mismatchedProcesses(this.successCode);
        if(onlyErrors){
            if(tempProcessList.size() > 0)
                runList(tempProcessList);
        }
        else{
            runList(this.processPool.processList);
        }
        return this;
    }
    public void runList(LinkedList<Process> processList){
        for(Process process: processList){
            this.threadPoolExecutor.submit(process);
        }
    }
}
