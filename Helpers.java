import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public class Helpers {
    
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
