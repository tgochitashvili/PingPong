import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class Ping{
    final static boolean DEBUG = false;
    
    
    public static void main(String[]args){
        if(args.length == 0){
            Helpers.printInfoAndQuit();
        }
        HashMap<String,String> argMap = Helpers.argsToMap(args);
        if(argMap.getOrDefault("valid", "true").equals("false")){
            Helpers.printInfoAndQuit();
        }
        boolean onlyErrors = false;
        try{
            
            run(argMap, onlyErrors);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void run(HashMap<String,String> args, boolean onlyErrors) throws IOException, InterruptedException{
        Scanner scnr = new Scanner(System.in);
        String urlpath = args.get("urlpath");
        String serverpath = args.get("serverpath");
        String token = args.get("token");
        int nThreads = Runtime.getRuntime().availableProcessors()*5;
        nThreads = Integer.parseInt(args.getOrDefault("threads", "" + nThreads));

        System.out.println("Threads: " + nThreads);

        Process.readTimeout = Integer.parseInt(args.getOrDefault("readTimeout", "1000"));
        Process.connTimeout = Integer.parseInt(args.getOrDefault("connTimeout", "1000"));

        System.out.println("Read timeout limit: " + Process.readTimeout + " ms");
        System.out.println("Connection timeout limit: " + Process.connTimeout + " ms");

        LinkedList<ProcessNode> processNodes = Helpers.buildNodes(urlpath, serverpath, token);
        long timeout = (Process.readTimeout + Process.connTimeout)*processNodes.get(0).processList.size();
        
        while(true){
            LinkedList<ThreadPoolWrapper> threadPoolList =  Helpers.buildPools(processNodes, nThreads);
            Helpers.runPools(threadPoolList, onlyErrors);
            Helpers.trackThreadPoolWrapperProgress(threadPoolList, timeout);
            Helpers.LoopAction action = Helpers.LoopAction.PROMPT;
            while(action == Helpers.LoopAction.PROMPT){
                System.out.println("(w) Write logs; (q) Quit; (r) Retry; (e) Retry only errors;");
                String response = "";
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
        // Helpers.log(threadPoolList);
        
    }
}