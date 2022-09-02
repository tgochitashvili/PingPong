import java.util.LinkedList;
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
}