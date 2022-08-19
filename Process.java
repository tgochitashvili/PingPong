import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URI;
import java.net.UnknownHostException;

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
            // InetAddress inet = InetAddress.fgetb(this.URLNode.URL);

            // URLNode.isChecked = inet.isReachable(timeout);

            URL url = new URL(URLNode.URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setConnectTimeout(timeout);
            con.setReadTimeout(timeout);
            con.connect();
            URLNode.response = con.getResponseCode() + " - " + con.getResponseMessage();
            con.disconnect();
        }
        catch(Exception e){
            URLNode.isChecked = true;
            URLNode.response = e.getMessage();
        }
        finally{
            synchronized(responseList) {
                responseList.add(URLNode);
            }
        }
        
        return;
    }
}
