import java.net.HttpURLConnection;
import java.net.URL;

import java.util.List;


public class Process implements Runnable {
    public int timeout;
    public UrlNode URLNode;
    public List<UrlNode> responseList;
    public Process(String URL, int timeout, List<UrlNode> responseList){
        this.timeout = timeout;
        URLNode = new UrlNode(URL);
        this.responseList = responseList;
    }
    private static final String USER_AGENT = "Mozilla/5.0";
    public void run(){
        try{
            URL url = new URL(URLNode.URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
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
