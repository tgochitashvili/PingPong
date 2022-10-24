import java.util.LinkedList;
public class ProcessesWrapper{
    public LinkedList<Process> processList;
    public String serverName;
    public ProcessesWrapper(LinkedList<Process> processList, String serverName){
        this.processList = new LinkedList<Process>(processList);
        this.serverName = serverName;
    }
    public ProcessesWrapper(LinkedList<Process> processList){
        this.processList = new LinkedList<Process>(processList);
        this.serverName = "";
    }
    public ProcessesWrapper(String serverName){
        this.processList = new LinkedList<Process>();
        this.serverName = serverName;
    }
    public ProcessesWrapper(){
        this.processList = new LinkedList<Process>();
        this.serverName = "";
    }

    public LinkedList<Process> mismatchedProcesses(String responseCode){
        LinkedList<Process> tempProcessList = new LinkedList<Process>();
        for(Process process: this.processList){
            if(!process.checkResponse(responseCode)){
                tempProcessList.add(process);
            }
        }
        return tempProcessList;
    }
}