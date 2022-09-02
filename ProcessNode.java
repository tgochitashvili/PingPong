import java.util.LinkedList;
public class ProcessNode{
    LinkedList<Process> processList;
    public ProcessNode(LinkedList<Process> processList){
        this.processList = new LinkedList<Process>(processList);
    }
    public ProcessNode(){
        this.processList = new LinkedList<Process>();
    }
}