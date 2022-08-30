import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.ranges.RangeException;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;


public class Ping{
    final static boolean DEBUG = false;
    
    
    public static void main(String[]args) throws UnknownHostException, IOException, RangeException{
        if(args.length == 0){
            System.out.println("Please enter some arguments:");
            System.out.println("\t threads=x\t\t - Amount of threads to use for pinging");
            System.out.println("\t readtimeout=x\t\t - timeout limit for the response in milliseconds (default 1000)");
            System.out.println("\t conntimeout=x\t\t - timeout limit for the connection in milliseconds (default 1000)");
        }
        run(Helpers.argsToMap(args));
    }

    public static void run(HashMap<String,String> args){
        System.out.println(args);
        String foldername = "./urls";
        File folder = new File(foldername);
        List<String> urlsList = new LinkedList<String>();
        if(!folder.isDirectory()){
            System.out.println("Invalid path supplied, please enter a path to a folder.");
            System.exit(-1);
        }
        else{
            urlsList.addAll(Helpers.walk(folder, DEBUG));
            if(DEBUG)
                for(String url: urlsList)
                    System.out.println(url);
        }
        LinkedList<UrlNode> nodeList = new LinkedList<UrlNode>();

        int nThreads = Runtime.getRuntime().availableProcessors()*5;
        nThreads = Integer.parseInt(args.get("threads"));

        System.out.println("Threads: " + nThreads);

        Process.readTimeout = Integer.parseInt(args.getOrDefault("readTimeout","1000"));
        Process.connTimeout = Integer.parseInt(args.getOrDefault("connTimeout", "1000"));

        System.out.println("Read timeout limit: " + Process.readTimeout + " ms");
        System.out.println("Connection timeout limit: " + Process.connTimeout + " ms");

        long timeout = (Process.readTimeout + Process.connTimeout)*urlsList.size();
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        for(String url:urlsList){
            threadPool.submit(
                new Process(url)
            );
        }
        long currentTasks = threadPool.getCompletedTaskCount();
        long numTasks = urlsList.size();
        try{
            threadPool.shutdown();
            System.out.println();
            long start = System.currentTimeMillis();
            String[] symbArray = {"/","-","\\","|"};
            LinkedList<String> symbols = new LinkedList<String>(Arrays.asList(symbArray));
            for(;
                currentTasks < numTasks && System.currentTimeMillis()-start<timeout;
                currentTasks = threadPool.getCompletedTaskCount()
                ){
                System.out.printf(Helpers.CLEARLINE() + "Tasks finished: " + currentTasks + "/" + numTasks + "  " + symbols.getFirst(), "");
                symbols.add(symbols.removeFirst());
                Thread.sleep(100);
            }
            System.out.printf(Helpers.CLEARLINE() + "Tasks finished: " + currentTasks + "/" + numTasks, "");
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            System.out.println("\nDONE!");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
        FileWriter fWriter = null;
        BufferedWriter bWriter =  null;
        PrintWriter pWriter = null;
        File out = null;
        try{
            out = new File("./Logs/Log-" + System.currentTimeMillis()+".txt");
            fWriter = new FileWriter(out);
            bWriter = new BufferedWriter(fWriter);
            pWriter = new PrintWriter(bWriter);
            for(UrlNode node: nodeList){
                pWriter.println(node.URL + ": " + node.getFormattedResponse());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(pWriter!=null){
                pWriter.flush();
                pWriter.close();
            }
        }
    }
}