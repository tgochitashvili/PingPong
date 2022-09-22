import java.util.HashMap;

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

    public static String CLEARLINE(int length){
        return "\r%" + length + "s\r";
    }

    public static String CLEARLINE(){
        return "\r%50s\r";
    }
}
