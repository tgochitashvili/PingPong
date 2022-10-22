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
        try{
            run(argMap);
            System.out.println("DONE");
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            return;
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
            ThreadPoolExecutor tempExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
            ThreadPoolWrapper tempWrapper = new ThreadPoolWrapper()
                                                .setServerName(processNode.serverName)
                                                .setExecutor(tempExecutor)
                                                .setProcessNode(processNode);

            tempWrapper.runProcesses(false);
            threadPoolList.add(tempWrapper);
        }
        
        Helpers.trackThreadPoolWrapperProgress(threadPoolList, timeout);
        
        FileWriter fWriter = null;
        BufferedWriter bWriter =  null;
        PrintWriter pWriter = null;
        File out = null;
        SimpleDateFormat sDateFormat = new SimpleDateFormat("dd-MMM-yy_HH-mm-ss");    
        try{
            for(ProcessNode processNode: processNodes){

                out = new File("./Logs/" + sDateFormat.format(new Date(System.currentTimeMillis())) + "-" + processNode.serverName + ".txt");
                fWriter = new FileWriter(out);
                bWriter = new BufferedWriter(fWriter);
                pWriter = new PrintWriter(bWriter, true);
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
            } else {
                if(bWriter!=null){
                    bWriter.flush();
                    bWriter.close();
                }
                else{
                    if(fWriter!=null){
                        fWriter.flush();
                        fWriter.close();
                    }
                }
            }
        }
    }
}