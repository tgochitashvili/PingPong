package com.tgoch;


import java.util.LinkedList;
import java.util.Scanner;

import java.util.HashMap;

import java.io.IOException;


public class Ping{
    public static void main(String[]args){
        if(args.length == 0){
            Helpers.printInfoAndQuit();
        }
        HashMap<String,String> argMap = Helpers.argsToMap(args);
        if(argMap.getOrDefault("valid", "true").equals("false")){
            Helpers.printInfoAndQuit();
        }
        boolean onlyErrors = false;
        try (Scanner scnr = new Scanner(System.in)) {
            run(argMap, onlyErrors, scnr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void run(HashMap<String,String> args, boolean onlyErrors, Scanner scnr) throws InterruptedException{
        
        String urlpath = args.get("urlpath");
        String serverpath = args.getOrDefault("serverpath","");
        String token = args.getOrDefault("token","");
        int nThreads = Integer.parseInt(args.getOrDefault("threads",
                                                            "" + Runtime.getRuntime().availableProcessors()*5));

        System.out.println("Threads: " + nThreads);

        Process.readTimeout = Integer.parseInt(args.getOrDefault("readTimeout", "1000"));
        Process.connTimeout = Integer.parseInt(args.getOrDefault("connTimeout", "1000"));
        String successCode = args.getOrDefault("successcode", "200");

        

        System.out.println("Read timeout limit: " + Process.readTimeout + " ms");
        System.out.println("Connection timeout limit: " + Process.connTimeout + " ms");

        LinkedList<ProcessPool> processPools = Helpers.buildProcessPools(
                urlpath,
                serverpath,
                token,
                Helpers.FileType.TXT
        );
        long timeout = (long) (Process.readTimeout + Process.connTimeout) *processPools.get(0).getProcessList().size();
        Thread.sleep(1000);
        while(true){
            LinkedList<ThreadPoolWrapper> threadPoolList =  Helpers.buildThreadPools(processPools, nThreads, successCode);
            Helpers.runPools(threadPoolList, onlyErrors);
            Helpers.trackThreadPoolWrapperProgress(threadPoolList, timeout, successCode);
            while(true){
                System.out.println("(q) Quit; (r) Retry; (e) Retry errors;");
                String response;
                try{
                    try{
                        Helpers.log(threadPoolList, onlyErrors);
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }
                    response =  scnr.next();
                    onlyErrors = response.contains("e");
                    if(response.contains("q"))
                        return;
                    if(response.contains("r") || onlyErrors)
                        break;
                }
                catch(Exception e){
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
        }
    }
}
