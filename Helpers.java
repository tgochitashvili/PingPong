import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

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
    
    public static List<String> walk(File folder, boolean DEBUG){
        if(DEBUG)
            System.out.println("Reading from folder: " + folder.getName());
        List<String> urlsArray = new LinkedList<String>();

        for(File file: folder.listFiles()){
            if(file.isDirectory()){
                urlsArray.addAll(walk(file,DEBUG));
            }
            else {
                Scanner scnr = null;
                try{
                    scnr = new Scanner(file);
                    while(scnr.hasNextLine())
                        urlsArray.add(scnr.nextLine());
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

    public static String CLEARLINE(int length){
        return "\r%" + length + "s\r";
    }
    public static String CLEARLINE(){
        return "\r%50s\r";
    }
}
