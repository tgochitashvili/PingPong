import java.util.LinkedList; 

public interface AbstractReader {
    /**
     * This method will look for urls in the urlPath, look for the token in every url, and replace them
     * with servers from serverPath and return ProcessNode lists for each server.
     */
    public LinkedList<ProcessesWrapper> getProcessNodesFromSource(String urlPath, String serverPath, String token);
    /*
     * This method will look for urls in the urlPath and return a single ProcessNode list.
     */
    public LinkedList<ProcessesWrapper> getProcessNodesFromSource(String urlPath);

}