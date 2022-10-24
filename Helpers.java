import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ThreadPoolExecutor;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Helpers {

    public enum LoopAction {
        EXIT,
        CONTINUE,
        ONLYERRORS,
        PROMPT
    }

    public enum LogType{
        JSON,
        TXT
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
        System.exit(-1);
    }

    public static LinkedList<ProcessesWrapper> buildNodes(String urlpath, String serverpath, String token){
        AbstractReader plainReader = new TokenizedPlainReader();
        LinkedList<ProcessesWrapper> processNodes = plainReader.getProcessNodesFromSource(urlpath, serverpath, token);
        return processNodes;
    }

    public static LinkedList<ThreadPoolWrapper> buildPools(LinkedList<ProcessesWrapper> processNodes, int nThreads, String successCode){
        
        LinkedList<ThreadPoolWrapper> threadPoolList = new LinkedList<ThreadPoolWrapper>();
        for(ProcessesWrapper processNode:processNodes){
            threadPoolList.add(
                new ThreadPoolWrapper()
                                    .setServerName(processNode.serverName)
                                    .setExecutor(
                                            (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads)
                                        )
                                    .setProcessNode(processNode)
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

    public static void trackThreadPoolWrapperProgress(LinkedList<ThreadPoolWrapper> poolList, long timeout) throws InterruptedException{
        long start = System.currentTimeMillis();
        boolean allDone;
        while((System.currentTimeMillis()-start) < timeout){
            CLEARSCREEN();
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
            Thread.sleep(50);
        }

        for(ThreadPoolWrapper threadPoolWrapper: poolList){
            threadPoolWrapper.getExecutor().shutdown();
            threadPoolWrapper.getExecutor().awaitTermination(timeout, TimeUnit.MILLISECONDS);
        }
    }

    public static void log(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors, LogType logType) throws IOException{
        switch(logType){
            case JSON:
                logJSON(threadPoolWrappers, onlyErrors);
            default:
                logTxt(threadPoolWrappers, onlyErrors);
        }
    }

    public static void logJSON(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors) throws IOException{
    
    }

    public static void logTxt(LinkedList<ThreadPoolWrapper> threadPoolWrappers, boolean onlyErrors) throws IOException{
        
        FileWriter fWriter = null;
        BufferedWriter bWriter =  null;
        PrintWriter pWriter = null;
        File out = null;

        SimpleDateFormat sDateFormat = new SimpleDateFormat("dd-MMM-yy_HH-mm-ss");    
        try{
            for(ThreadPoolWrapper threadPoolWrapper: threadPoolWrappers){
                ProcessesWrapper processNode = threadPoolWrapper.getProcessNode();
                LinkedList<Process> processList = onlyErrors?
                                processNode.mismatchedProcesses(threadPoolWrapper.getSuccessCode())
                                :processNode.processList;
                out = new File("./Logs/" + sDateFormat.format(new Date(System.currentTimeMillis())) + "-" + threadPoolWrapper.getServerName() + ".txt");
                fWriter = new FileWriter(out);
                bWriter = new BufferedWriter(fWriter);
                pWriter = new PrintWriter(bWriter, true);
                for(Process process: processList){
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

    public static LoopAction continueLoop(LinkedList<ThreadPoolWrapper> threadPoolWrappers, String response) throws IOException{
        LoopAction action = LoopAction.PROMPT;
        boolean onlyErrors = response.contains("e");
        if(response.contains("w"))
            log(threadPoolWrappers, onlyErrors, LogType.TXT);

        if(response.contains("r"))
            action = LoopAction.CONTINUE;
        
        if(onlyErrors)
            action = LoopAction.ONLYERRORS;

        if(response.contains("q"))
            action = LoopAction.EXIT;
        return action;
    }
}
