import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;


public class Process implements Runnable {
    public static int connTimeout = 1000;
    public static int readTimeout = 1000;
    public static List<UrlNode> responseList;
    public UrlNode URLNode;
    public Process(String URL){
        URLNode = new UrlNode(URL);
    }
    private static final String USER_AGENT = "Mozilla/5.0";
    public void run(){
        try{
            URL url = new URL(URLNode.URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setConnectTimeout(connTimeout);
            con.setReadTimeout(readTimeout);
            con.connect();

            URLNode.response = con.getResponseMessage();
            URLNode.responseCode = "" + con.getResponseCode();
            
            con.disconnect();
        }
        catch(Exception e){
            URLNode.response = e.getMessage();
            URLNode.responseCode = "-1";
        }
        finally{
            synchronized(responseList) {
                responseList.add(URLNode);
            }
        }
        return;
    }
}
