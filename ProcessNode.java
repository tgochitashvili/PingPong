import java.io.File;
import java.util.LinkedList;
import org.json.JSONObject;
public class ProcessNode{
    public LinkedList<Process> processList;
    public String serverName;
    public ProcessNode(LinkedList<Process> processList, String serverName){
        this.processList = new LinkedList<Process>(processList);
        this.serverName = serverName;
    }
    public ProcessNode(LinkedList<Process> processList){
        this.processList = new LinkedList<Process>(processList);
        this.serverName = "";
    }
    public ProcessNode(String serverName){
        this.processList = new LinkedList<Process>();
        this.serverName = serverName;
    }
    public ProcessNode(){
        this.processList = new LinkedList<Process>();
        this.serverName = "";
    }
    
    public boolean writeToJSON(File logFolder){
        return false;
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