import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;
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

public class Helpers {

    public static HashMap<String,String> argsToMap(String[]args){
        HashMap<String,String> argMap = new HashMap<String,String>();
        for(int i = 0; i < args.length; i++){
            String[] splitArg = args[i].split("=");
            if(splitArg.length>2){
                System.out.println("Incorrect argument format");
                System.exit(-1);
            }
            argMap.put(splitArg[0],splitArg[1]);
        }
        if(!argMap.containsKey("urlpath")){
            argMap.putIfAbsent("valid", "false");
        }

        if(argMap.containsKey("serverpath")){
            if(!argMap.containsKey("token")){
                argMap.putIfAbsent("valid", "false");
            }
        }
        return argMap;
    }

    public static void CLEARSCREEN() {
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else{
                String[] stringArray = {"clear"};
                Runtime.getRuntime().exec(stringArray);
            }
        } catch (IOException | InterruptedException ex) {}
    }

    public static void printInfoAndQuit(){
        CLEARSCREEN();
        System.out.println("Please enter some arguments:");
        System.out.println("\t threads=x\t\t - Amount of threads to use for pinging (default: hardware threads * 5");
        System.out.println("\t readtimeout=x\t\t - timeout limit for the response in milliseconds (default: 1000)");
        System.out.println("\t conntimeout=x\t\t - timeout limit for the connection in milliseconds (default: 1000)");
        System.out.println("\t serverpath=x\t\t - path to the file containing the server list, only works with a tokenized url list, requires token");
        System.out.println("\t urlpath=x\t\t - mandatory, path to the file containing either full or tokenized URLs");
        System.out.println("\t token=x\t\t - mandatory for serverpath, token to look for in the urls and replace with the real servers, default is empty and will ignore the server list");
        
        System.exit(-1);
    }

    public static void trackThreadPoolWrapperProgress(LinkedList<ThreadPoolWrapper> poolList, long timeout) throws InterruptedException{
        long start = System.currentTimeMillis();
        boolean allDone;
        while((System.currentTimeMillis()-start) < timeout){
            allDone = true;
            for(ThreadPoolWrapper threadPoolWrapper: poolList){
                long numTasks = threadPoolWrapper.getExecutor().getTaskCount();
                long progress = threadPoolWrapper.getExecutor().getCompletedTaskCount();
                boolean currentDone = (progress >= numTasks);
                allDone &= currentDone;
                String toPrint = "[" + threadPoolWrapper.getServerName() + "]\t\t Progress : " + progress + "/" + numTasks;
                System.out.println(toPrint);
            }
            if(allDone){
                break;
            }
            allDone = true;
            Thread.sleep(200);
            CLEARSCREEN();
        }

        for(ThreadPoolWrapper threadPoolWrapper: poolList){
            threadPoolWrapper.getExecutor().shutdown();
            threadPoolWrapper.getExecutor().awaitTermination(timeout, TimeUnit.MILLISECONDS);
        }
    }
}
