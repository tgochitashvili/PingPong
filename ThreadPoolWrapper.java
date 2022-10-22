import java.util.LinkedList;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolWrapper{
    private String serverName;
    private ThreadPoolExecutor threadPoolExecutor;
    private ProcessNode processNode;
    private String successCode = "200";
    private LinkedList<Future<?>> futures;

    public ThreadPoolWrapper(){
        this.resetFutures();
    }
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
    public ThreadPoolWrapper setProcessNode(ProcessNode processNode){
        this.processNode = processNode;
        return this;
    }
    public ProcessNode getProcessNode(){
        return this.processNode;
    }
    public ThreadPoolWrapper setSuccessCode(String successCode){
        this.successCode = successCode;
        return this;
    }
    public String getSuccessCode(){
        return this.successCode;
    }
    public ThreadPoolWrapper resetFutures(){
        futures = new LinkedList<Future<?>>();
        return this;
    }
    public LinkedList<Future<?>> getFutures(){
        return this.futures;
    }

    public boolean runProcesses(boolean onlyErrors){
        if(this.processNode.processList.size() < 0){
            return false;
        }
        LinkedList<Process> tempProcessList = this.processNode
                                                    .mismatchedProcesses(this.successCode);
        if(onlyErrors){
            if(tempProcessList.size() > 0)
                runList(tempProcessList);
            return true;
        }
        runList(this.processNode.processList);
        return true;
    }
    public void runList(LinkedList<Process> processList){
        for(Process process: processList){
            futures.add(this.threadPoolExecutor.submit(process));
        }
    }
}
