import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
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
        try{
            run(argMap);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void run(HashMap<String,String> args) throws IOException, InterruptedException{
        String urlpath = args.get("urlpath");
        String serverpath = args.get("serverpath");
        String token = args.get("token");
        int nThreads = Runtime.getRuntime().availableProcessors()*5;
        nThreads = Integer.parseInt(args.getOrDefault("threads", "10"));

        System.out.println("Threads: " + nThreads);

        Process.readTimeout = Integer.parseInt(args.getOrDefault("readTimeout", "1000"));
        Process.connTimeout = Integer.parseInt(args.getOrDefault("connTimeout", "1000"));

        System.out.println("Read timeout limit: " + Process.readTimeout + " ms");
        System.out.println("Connection timeout limit: " + Process.connTimeout + " ms");

        
        AbstractReader plainReader = new TokenizedPlainReader();
        LinkedList<ProcessNode> processNodes = plainReader.getProcessNodesFromSource(urlpath, serverpath, token);
        long timeout = (Process.readTimeout + Process.connTimeout)*processNodes.get(0).processList.size();
        LinkedList<ThreadPoolWrapper> threadPoolList = new LinkedList<ThreadPoolWrapper>();
        for(ProcessNode processNode:processNodes){
            threadPoolList.add(
                new ThreadPoolWrapper()
                                    .setServerName(processNode.serverName)
                                    .setExecutor(
                                            (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads)
                                        )
                                    .setProcessNode(processNode)
                                    .runProcesses(false)
                                    );
        }

        Thread.sleep(2000);
        Helpers.trackThreadPoolWrapperProgress(threadPoolList, timeout);
        Helpers.log(threadPoolList);
    }
}