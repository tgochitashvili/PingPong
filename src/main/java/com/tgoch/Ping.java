package com.tgoch;


import java.util.LinkedList;
import java.util.Scanner;

import com.tgoch.Helpers.FileType;

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
        Scanner scnr = new Scanner(System.in);
        try{
            run(argMap, onlyErrors, scnr);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            scnr.close();
        }
    }

    public static void run(HashMap<String,String> args, boolean onlyErrors, Scanner scnr) throws IOException, InterruptedException{
        
        String urlpath = args.get("urlpath");
        String serverpath = args.getOrDefault("serverpath","");
        String token = args.getOrDefault("token","");
        int nThreads = Runtime.getRuntime().availableProcessors()*5;
        nThreads = Integer.parseInt(args.getOrDefault("threads", "" + nThreads));

        System.out.println("Threads: " + nThreads);

        Process.readTimeout = Integer.parseInt(args.getOrDefault("readTimeout", "1000"));
        Process.connTimeout = Integer.parseInt(args.getOrDefault("connTimeout", "1000"));
        String successCode = args.getOrDefault("successcode", "200");

        System.out.println("Read timeout limit: " + Process.readTimeout + " ms");
        System.out.println("Connection timeout limit: " + Process.connTimeout + " ms");

        LinkedList<ProcessPool> processPools = Helpers.buildProcessPools(urlpath, serverpath, token);
        long timeout = (Process.readTimeout + Process.connTimeout)*processPools.get(0).getProcessList().size();
        Thread.sleep(1000);
        while(true){
            LinkedList<ThreadPoolWrapper> threadPoolList =  Helpers.buildThreadPools(processPools, nThreads, successCode);
            Helpers.runPools(threadPoolList, onlyErrors);
            Helpers.trackThreadPoolWrapperProgress(threadPoolList, timeout, successCode);
            Helpers.LoopAction action = Helpers.LoopAction.PROMPT;
            while(action == Helpers.LoopAction.PROMPT){
                System.out.println("(w) Write logs; (q) Quit; (r) Retry; (e) Retry errors;");
                String response = successCode;
                try{
                    response =  scnr.next();
                    action = Helpers.continueLoop(threadPoolList, response);
                    onlyErrors = (action == Helpers.LoopAction.ONLYERRORS);
                    switch(action){
                        case EXIT:
                            return;
                        default:
                            continue;
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
            
        }
    }
}
