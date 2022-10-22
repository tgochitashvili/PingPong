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
        HttpURLConnection con = null;
        try{
            URL url = new URL(URLNode.URL);
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setConnectTimeout(connTimeout);
            con.setReadTimeout(readTimeout);
            con.connect();

            URLNode.response = con.getResponseMessage();
            URLNode.responseCode = "" + con.getResponseCode();
        }
        catch(Exception e){
            URLNode.response = e.getMessage();
            URLNode.responseCode = "-1";
        }
        finally{
            if(con != null)
                con.disconnect();
            synchronized(responseList) {
                responseList.add(URLNode);
            }
        }
        return;
    }
    public boolean checkResponse(String responseCode){
        return this.URLNode.checkResponse(responseCode);
    }
}
