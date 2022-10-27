package com.tgoch;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;


public class Readers{
    public static LinkedList<ProcessPool> getProcessPoolsTxt(String urlPath, String serverPath, String token){
        LinkedList<ProcessPool> processList = new LinkedList<ProcessPool>();
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
            replaceWithToken(token, processList, servers, urls);
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
    
    public static LinkedList<ProcessPool> getProcessPoolsTxt(String urlPath){
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
                if(!url.equals(""))
                    processPool.addProcess(new Process(url));
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


    private static void replaceWithToken(String token, LinkedList<ProcessPool> processList, LinkedList<String> servers,
            LinkedList<String> urls) {
        ProcessPool processPool;
        for(String server: servers){
            processPool = new ProcessPool(server);
            for(String url: urls){
                if(!url.equals("")){
                    String tempUrl = url.replaceAll(token,server);
                    processPool.addProcess(new Process(tempUrl));
                }
            }
            processList.add(processPool);
        }
    }
}
