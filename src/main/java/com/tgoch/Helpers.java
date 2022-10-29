package com.tgoch;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Helpers {

    private static Path logPath = null;

    public static long loopTimeMillis = 200;

    public enum LoopAction {
        EXIT,
        CONTINUE,
        ONLYERRORS,
        PROMPT
    }

    public enum FileType{
        JSON("json"),
        TXT("txt");

        String key;

        FileType(String key){
            this.key = key;
        }

        static FileType getValue(String in){
            in = in.toLowerCase();
            switch(in){
                case "json":
                    return FileType.JSON;
                default:
                    return FileType.TXT;
            }
        }
    }

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
        System.out.println("\t threads=x\t\t - Amount of threads to use for pinging (default: hardware threads * 5)");
        System.out.println("\t readtimeout=x\t\t - timeout limit for the response in milliseconds (default: 1000)");
        System.out.println("\t conntimeout=x\t\t - timeout limit for the connection in milliseconds (default: 1000)");
        System.out.println("\t serverpath=x\t\t - path to the file containing the server list, only works with a tokenized url list, requires token");
        System.out.println("\t urlpath=x\t\t - mandatory, path to the file containing either full or tokenized URLs");
        System.out.println("\t token=x\t\t - token to look for in the urls and replace with the real servers, default is empty and will ignore the server list");
        System.out.println("\t successcode=x\t\t - success code to check against for errors (default: 200)");
        System.out.println("\t logtype=x\t\t - type of logging to use (default: json)");
        System.exit(-1);
    }

    public static LinkedList<ProcessPool> buildProcessPools(String urlpath, String serverpath, String token) throws IOException{
        LinkedList<ProcessPool> processPools = !serverpath.equals("") && !token.equals("") 
                                                ? Readers.getProcessPoolsTxt(urlpath, serverpath, token)
                                                : Readers.getProcessPoolsTxt(urlpath);

        return processPools;
    }

    public static LinkedList<ThreadPoolWrapper> buildThreadPools(LinkedList<ProcessPool> processPools, int nThreads, String successCode){
        
        LinkedList<ThreadPoolWrapper> threadPoolList = new LinkedList<ThreadPoolWrapper>();
        for(ProcessPool processPool:processPools){
            threadPoolList.add(
                new ThreadPoolWrapper()
                                    .setServerName(processPool.serverName)
                                    .setExecutor(
                                            (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads)
                                        )
                                    .setProcessPool(processPool)
                                    .setSuccessCode(successCode)
                                    );
        }
        return threadPoolList;
    }

    public static LinkedList<ThreadPoolWrapper> runPools(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors){
        for(ThreadPoolWrapper threadPoolWrapper: threadPoolWrappers){
            threadPoolWrapper.runProcesses(onlyErrors);
        }
        return threadPoolWrappers;
    }

    public static void trackThreadPoolWrapperProgress(LinkedList<ThreadPoolWrapper> poolList, long timeout, String successCode) throws InterruptedException{
        timeout = (long) (timeout*2);
        long start = System.currentTimeMillis();
        boolean allDone;
        while((System.currentTimeMillis()-start) < timeout){
            allDone = true;
            String printBuffer = "";
            for(ThreadPoolWrapper threadPoolWrapper: poolList){
                long numTasks = threadPoolWrapper.getExecutor().getTaskCount();
                long progress = threadPoolWrapper.getExecutor().getCompletedTaskCount();
                boolean currentDone = (progress >= numTasks);
                allDone &= currentDone;
                int numErrors = (threadPoolWrapper.getProcessPool().mismatchedProcesses(successCode)).size();
                String toPrint = "[" + threadPoolWrapper.getServerName() + "]\t\t Progress : " + progress + "/" + numTasks + ";";
                if(numErrors > 0)
                    toPrint += " Errors: " + numErrors;
                printBuffer += toPrint + "\n";
            }
            if(allDone){
                break;
            }
            allDone = true;
            CLEARSCREEN();
            System.out.print(printBuffer);
            printBuffer = "";
            Thread.sleep(loopTimeMillis);
        }

        for(ThreadPoolWrapper threadPoolWrapper: poolList){
            threadPoolWrapper.getExecutor().shutdown();
            threadPoolWrapper.getExecutor().awaitTermination(timeout, TimeUnit.MILLISECONDS);
        }
    }

    public static void log(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors, FileType logType) throws IOException{
        switch(logType){
            case JSON:
                logJSON(threadPoolWrappers, onlyErrors);
                break;
            default:
                logTxt(threadPoolWrappers, onlyErrors);
        }
    }

    public static void logJSON(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors) throws IOException{
        
        FileWriter fWriter = null;
        BufferedWriter bWriter =  null;
        File out = null;
       
        Path path = getLogPath();

        try{            
            for(ThreadPoolWrapper threadPoolWrapper: threadPoolWrappers){
                renameFilesToTemp(getFiles(path));
                String currTime = "" + System.currentTimeMillis();
                out = new File(path + "/" + threadPoolWrapper.getServerName() + "-" + (currTime.substring(currTime.length()-5).hashCode() + ".json"));
                fWriter = new FileWriter(out);
                bWriter = new BufferedWriter(fWriter);
                String log = threadPoolWrapper.toJSON().toString(4);
                bWriter.write(log);
                bWriter.flush();
                deleteFiles(path, Pattern.compile("*.temp"));
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
        finally{
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

    public static LinkedList<File> getFiles(Path path){
        return new LinkedList<File>(Arrays.asList(path.toFile().listFiles()));
    }
    
    public static LinkedList<File> renameFilesToTemp(LinkedList<File> files){
        for(File file: files){
            file.renameTo(new File(file.getPath() + ".temp"));
        }
        return files;
    }

    public static void deleteFiles(Path path, Pattern pattern){
        LinkedList<File> files = getFiles(path);
        for(File file: files){
            if(pattern.matcher(file.getName()).matches()){
                file.delete();
            }
        }
    }

    public static Path getLogPath() {
        if(logPath!=null)
            return logPath;
        
        String logDate = "";
        Path path = null;
        try{
            logDate = formatCurrentDate();
            path = Files.createDirectories(Paths.get("./Logs/" + logDate + "/"));  
        }
        catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        }
        logPath = path;
        return path;
    }

    public static String formatCurrentDate() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss"); 
        String logDate = sDateFormat.format(new Date(System.currentTimeMillis()));
        return logDate;
    }

    public static void logTxt(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors) throws IOException{
        
        FileWriter fWriter = null;
        BufferedWriter bWriter =  null;
        PrintWriter pWriter = null;
        File out = null;

        SimpleDateFormat sDateFormat = new SimpleDateFormat("dd-MMM-yy_HH-mm-ss");    
        try{
            for(ThreadPoolWrapper threadPoolWrapper: threadPoolWrappers){
                ProcessPool processPool = threadPoolWrapper.getProcessPool();
                LinkedList<Process> processList = onlyErrors?
                                processPool.mismatchedProcesses(threadPoolWrapper.getSuccessCode())
                                :processPool.getProcessList();
                Files.createDirectories(Paths.get("./Logs/"));
                out = new File("./Logs/" + sDateFormat.format(new Date(System.currentTimeMillis())) + "-" + threadPoolWrapper.getServerName() + ".txt");
                fWriter = new FileWriter(out);
                bWriter = new BufferedWriter(fWriter);
                pWriter = new PrintWriter(bWriter, true);
                for(Process process: processList){
                    pWriter.println(process.URLNode.getURL() + ": " + process.URLNode.getLastFormattedResponse());
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
