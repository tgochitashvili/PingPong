import java.net.UnknownHostException;
import java.util.Arrays;
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
    
    
    public static void main(String[]args) throws UnknownHostException, IOException{
        if(args.length == 0){
            
            System.out.println("Please enter some arguments:");
            System.out.println("\t threads=x\t\t - Amount of threads to use for pinging");
            System.out.println("\t readtimeout=x\t\t - timeout limit for the response in milliseconds (default 1000)");
            System.out.println("\t conntimeout=x\t\t - timeout limit for the connection in milliseconds (default 1000)");
            System.out.println("\t serverpath=x\t\t - path to the file containing the server list, only works with a tokenized url list");
            System.out.println("\t urlpath=x\t\t - path to the file containing either full or tokenized URLs");
            System.out.println("\t token=x\t\t - token to look for in the urls and replace with the real servers, default is empty and will ignore the server list");
            
            System.exit(-1);
        }
        HashMap<String,String> argMap = Helpers.argsToMap(args);

        run(argMap);
    }

    public static void run(HashMap<String,String> args){
        TokenizedPlainReader plainReader = new TokenizedPlainReader();
        String urlpath = args.get("urlpath");
        String serverpath = args.get("serverpath");
        String token = args.get("token");
        LinkedList<ProcessNode> processNodes = plainReader.getProcessNodesFromSource(urlpath, serverpath, token);
        // System.exit(-1);
        int nThreads = Runtime.getRuntime().availableProcessors()*5;
        nThreads = Integer.parseInt(args.get("threads"));

        System.out.println("Threads: " + nThreads);

        Process.readTimeout = Integer.parseInt(args.getOrDefault("readTimeout", "1000"));
        Process.connTimeout = Integer.parseInt(args.getOrDefault("connTimeout", "1000"));

        System.out.println("Read timeout limit: " + Process.readTimeout + " ms");
        System.out.println("Connection timeout limit: " + Process.connTimeout + " ms");


        long timeout = (Process.readTimeout + Process.connTimeout)*processNodes.get(0).processList.size();
        LinkedList<ThreadPoolExecutor> threadPoolList = new LinkedList<ThreadPoolExecutor>();
        for(ProcessNode processNode:processNodes){
            threadPoolList.add((ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads));
            for(Process process: processNode.processList){
                threadPoolList.getLast().submit(process);
            }
        }
        for(ThreadPoolExecutor threadPool: threadPoolList){
            long currentTasks = threadPool.getCompletedTaskCount();
            long numTasks = threadPool.getTaskCount();
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
            for(ProcessNode processNode: processNodes){
                for(Process process: processNode.processList){
                    pWriter.println(process.URLNode.URL + ": " + process.URLNode.getFormattedResponse());
                }
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