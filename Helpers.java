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
import java.util.concurrent.Future;
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

        return argMap;
    }

    public static void CLEARSCREEN(){
        System.out.printf("\033[H\033[2J","");
    }

    public static void trackThreadPoolWrapperProgress(LinkedList<ThreadPoolWrapper> poolList, long timeout) throws InterruptedException{
        long start = System.currentTimeMillis();
        boolean allDone;
        int stringLength;
        while((System.currentTimeMillis()-start) < timeout){
            stringLength = 0;
            allDone = true;
            for(ThreadPoolWrapper threadPoolWrapper: poolList){
                long numTasks = threadPoolWrapper.getExecutor().getTaskCount();
                int progress = 0;
                for(Future<?> future: threadPoolWrapper.getFutures()){
                    progress+=future.isDone()?1:0;
                }
                boolean currentDone = (progress >= numTasks);
                allDone &= currentDone;
                String toPrint = "[" + threadPoolWrapper.getServerName() + "]\t\t Progress : " + progress + "/" + numTasks;
                stringLength += toPrint.length();
                System.out.println(toPrint);
            }
            if(allDone){
                break;
            }
            stringLength += poolList.size();
            allDone = true;
            Thread.sleep(200);
            stringLength = 0;
            CLEARSCREEN();
        }

        for(ThreadPoolWrapper threadPoolWrapper: poolList){
            threadPoolWrapper.getExecutor().shutdown();
            threadPoolWrapper.getExecutor().awaitTermination(timeout, TimeUnit.MILLISECONDS);
        }
    }
}
