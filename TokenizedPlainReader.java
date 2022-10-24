import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;


public class TokenizedPlainReader implements AbstractReader{

    public LinkedList<ProcessesWrapper> getProcessNodesFromSource(String urlPath, String serverPath, String token){
        LinkedList<ProcessesWrapper> processList = new LinkedList<ProcessesWrapper>();
        ProcessesWrapper processNode = null;
        Scanner urlScnr = null;
        Scanner serverScnr = null;
        try{
            File urlFile = new File(urlPath);
            urlScnr = new Scanner(urlFile);
            File serverFile = new File(serverPath);
            serverScnr = new Scanner(serverFile);
            LinkedList<String> servers = new LinkedList<String>();
            LinkedList<String> urls = new LinkedList<String>();
            while(serverScnr.hasNextLine()){
                String tempStr = serverScnr.nextLine();
                servers.add(tempStr);
            }
            while(urlScnr.hasNextLine()){
                urls.add(urlScnr.nextLine());
            }
            for(String server: servers){
                processNode = new ProcessesWrapper(server);
                for(String url: urls){
                    String tempUrl = url.replaceAll(token,server);
                    processNode.processList.add(new Process(tempUrl));
                }
                processList.add(processNode);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(urlScnr != null){
                urlScnr.close();
            }
            if(serverScnr != null){
                serverScnr.close();
            }
        }
        return processList;
    }

    public LinkedList<ProcessesWrapper> getProcessNodesFromSource(String urlPath){
        LinkedList<ProcessesWrapper> processList = null;
        ProcessesWrapper processNode = null;
        Scanner scnr = null;
        try{
            File urlFile = new File(urlPath);
            scnr = new Scanner(urlFile);
            LinkedList<String> urls = new LinkedList<String>();
            while(scnr.hasNextLine()){
                urls.add(scnr.nextLine());
            }
            processNode = new ProcessesWrapper();
            for(String url: urls){
                processNode.processList.add(new Process(url));
            }
            processList = new LinkedList<ProcessesWrapper>();
            processList.add(processNode);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally{
            if(scnr != null){
                scnr.close();
            }
        }
        return processList;
    }
}
