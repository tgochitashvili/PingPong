import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
        String foldername = ".\\urls";
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
        System.out.println("Threads: " + nThreads);
        int ttl = 1000;
        long timeout = ttl*urlsList.size();
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        
        for(String url:urlsList){
            threadPool.submit(
                new Process(url, ttl, nodeList)
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
                System.out.printf(Helpers.CLEARLINE() + "Requests executed: " + currentTasks + "/" + numTasks + "  " + symbols.getFirst(), "");
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