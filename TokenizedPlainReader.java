import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;


public class TokenizedPlainReader implements AbstractReader{

    public LinkedList<ProcessPool> getProcessPoolsFromSource(String urlPath, String serverPath, String token){
        LinkedList<ProcessPool> processList = new LinkedList<ProcessPool>();
        ProcessPool processPool = null;
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
                processPool = new ProcessPool(server);
                for(String url: urls){
                    String tempUrl = url.replaceAll(token,server);
                    processPool.processList.add(new Process(tempUrl));
                }
                processList.add(processPool);
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

    public LinkedList<ProcessPool> getProcessPoolsFromSource(String urlPath){
        LinkedList<ProcessPool> processList = null;
        ProcessPool processPool = null;
        Scanner scnr = null;
        try{
            File urlFile = new File(urlPath);
            scnr = new Scanner(urlFile);
            LinkedList<String> urls = new LinkedList<String>();
            while(scnr.hasNextLine()){
                urls.add(scnr.nextLine());
            }
            processPool = new ProcessPool();
            for(String url: urls){
                processPool.processList.add(new Process(url));
            }
            processList = new LinkedList<ProcessPool>();
            processList.add(processPool);
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
