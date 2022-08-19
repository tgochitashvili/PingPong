import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
public class Ping{
    final static boolean DEBUG = false;
    public static void main(String[]args) throws UnknownHostException, IOException{
        System.setProperty("sun.net.spi.nameservice.nameservers", "8.8.8.8");
        System.setProperty("sun.net.spi.nameservice.provider.1", "dns,sun");
        String foldername = ".\\urls";
        File folder = new File(foldername);
        List<String> urlsList = new LinkedList<String>();
        if(!folder.isDirectory()){
            System.out.println("Invalid path supplied, please enter a path to a folder.");
            System.exit(-1);
        }
        else{
            urlsList.addAll(walk(folder));
            if(DEBUG)
                for(String url: urlsList)
                    System.out.println(url);
        }
        LinkedList<UrlNode> nodeList = new LinkedList<UrlNode>();

        int nThreads = Runtime.getRuntime().availableProcessors()*5;
        System.out.println("Threads: " + nThreads);
        int ttl = 1000;
        ExecutorService threadPool = Executors.newFixedThreadPool(nThreads);
        
        for(String url:urlsList){
            threadPool.submit(
                new Process(url, ttl, nodeList)
            );
        }
        try{
            threadPool.shutdown();
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
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
                pWriter.println(node.URL + ": " + node.response);
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

    public static List<String> walk(File folder){
        if(DEBUG)
            System.out.println("Reading from folder: " + folder.getName());
        List<String> urlsArray = new LinkedList<String>();

        for(File file: folder.listFiles()){
            if(file.isDirectory()){
                urlsArray.addAll(walk(file));
            }
            else {
                Scanner scnr = null;
                try{
                    scnr = new Scanner(file);
                    while(scnr.hasNextLine())
                        urlsArray.add(scnr.nextLine());
                    scnr.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally{
                    if(scnr!=null)
                        scnr.close();
                }
            }
        }

        return urlsArray;
    }
}