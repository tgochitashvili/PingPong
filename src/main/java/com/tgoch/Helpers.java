package com.tgoch;

import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Helpers {

    private static Path logPath = null;

    public static long loopTimeMillis = 200;

    public enum FileType{
        JSON("json"),
        TXT("txt"),

        XLSX("xlsx");

        final String key;

        FileType(String key){
            this.key = key;
        }

        static FileType getValue(String in){
            in = in.toLowerCase();
            switch(in){
                case "json":
                    return FileType.JSON;
                case "xlsx":
                    return FileType.XLSX;
                default:
                    return FileType.TXT;
            }
        }
    }

    public static HashMap<String,String> argsToMap(String[]args){
        HashMap<String,String> argMap = new HashMap<>();
        for (String arg : args) {
            String[] splitArg = arg.split("=");
            if (splitArg.length > 2) {
                System.out.println("Incorrect argument format");
                System.exit(-1);
            }
            argMap.put(splitArg[0], splitArg[1]);
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
        } catch (IOException | InterruptedException ex) {ex.printStackTrace();}
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
        System.out.println("\t lightlog=x\t\t - will only log a single entry per request if true (default: false)");
        System.exit(-1);
    }

    public static LinkedList<ProcessPool> buildProcessPools(String urlpath, String serverpath, String token, FileType fileType) {
        if(fileType == FileType.TXT)
            return !serverpath.equals("") && !token.equals("")
                                                ? Readers.getProcessPoolsTxt(urlpath, serverpath, token)
                                                : Readers.getProcessPoolsTxt(urlpath);
        else{
            return Readers.getProcessPoolsXlsx(urlpath);
        }
    }

    public static LinkedList<ThreadPoolWrapper> buildThreadPools(LinkedList<ProcessPool> processPools, int nThreads, String successCode){
        
        LinkedList<ThreadPoolWrapper> threadPoolList = new LinkedList<>();
        for(ProcessPool processPool:processPools){
            threadPoolList.add(
                new ThreadPoolWrapper()
                                    .setServerName(processPool.serverName)
                                    .setExecutor(
                                            (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads)
                                        )
                                    .setProcessPool(processPool)
            );
        }
        return threadPoolList;
    }

    public static void runPools(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors){
        for(ThreadPoolWrapper threadPoolWrapper: threadPoolWrappers){
            threadPoolWrapper.runProcesses(onlyErrors);
        }
    }

    public static void trackThreadPoolWrapperProgress(LinkedList<ThreadPoolWrapper> poolList, long timeout, String successCode) throws InterruptedException{
        long start = System.currentTimeMillis();
        while((System.currentTimeMillis()-start) < timeout){
            boolean allDone = true;
            StringBuilder printBuffer = new StringBuilder();
            for(ThreadPoolWrapper threadPoolWrapper: poolList){
                ThreadPoolExecutor tempExecutor = threadPoolWrapper.getExecutor();
                long numTasks = tempExecutor.getTaskCount();
                long progress = tempExecutor.getCompletedTaskCount();
                boolean currentDone = (progress >= numTasks);
                allDone &= currentDone;
                int numErrors = (threadPoolWrapper.getProcessPool().mismatchedProcesses()).size();
                String toPrint;
                toPrint = "[" + threadPoolWrapper.getServerName() + "]\t\t Progress : " + progress + "/" + numTasks + "; \t" + (" Errors: " + numErrors);
                printBuffer.append(toPrint).append("\n");
            }
            CLEARSCREEN();
            System.out.print(printBuffer);
            if(allDone){
                break;
            }
            Thread.sleep(loopTimeMillis);
        }

        for(ThreadPoolWrapper threadPoolWrapper: poolList){
            threadPoolWrapper.getExecutor().shutdown();
            boolean awaitResult = threadPoolWrapper
                    .getExecutor()
                    .awaitTermination(timeout, TimeUnit.MILLISECONDS);
            if(!awaitResult){
                System.out.println("Timeout exceeded for: " + threadPoolWrapper.getServerName());
            }
        }
    }

    public static void log(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors) throws IOException{
        Path path = getLogPath();
        try{ 
            renameFilesToTemp(path);           
            for(ThreadPoolWrapper threadPoolWrapper: threadPoolWrappers){
                String currTime = "" + System.currentTimeMillis();
                String fileName = path
                        + "/" + threadPoolWrapper.getServerName()
                        + "-" + (currTime.substring(currTime.length()-5).hashCode() + ".json");
                try(
                        BufferedWriter bWriter = new BufferedWriter(
                                new FileWriter(
                                        fileName
                                )
                        )
                ){
                    String log = threadPoolWrapper.toJSON().toString(4);
                    bWriter.write(log);
                }
            }
            deleteFiles(path, ".temp");
        }
        catch(IOException e){
            e.printStackTrace();
        }     
    }

    public static LinkedList<File> getFiles(Path path){
        return new LinkedList<>(Arrays.asList(Objects.requireNonNull(path.toFile().listFiles())));
    }
    
    public static void renameFilesToTemp(Path path){
        LinkedList<File> files = getFiles(path);
        for(File file: files){
            if(file.isFile()){
                try {
                    if(!file.renameTo(new File(file.getPath() + ".temp"))){
                        throw new IOException("Could not rename file!");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void deleteFiles(Path path, String extension) throws IOException {
        LinkedList<File> files = getFiles(path);
        for(File file: files){
            if(file.getName().endsWith(extension)){
                if(!file.delete()){
                    throw new IOException("Could not delete file!");
                }
            }
        }
    }

    public static Path getLogPath() {
        if(logPath!=null)
            return logPath;
        
        String logDate;
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
        return sDateFormat.format(new Date(System.currentTimeMillis()));
    }
}
