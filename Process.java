import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class Process implements Runnable {
    public static int connTimeout = 1000;
    public static int readTimeout = 1000;
    public UrlNode URLNode;
    public Process(String URL){
        URLNode = new UrlNode(URL);
    }
    private static final String USER_AGENT = "Mozilla/5.0";
    public void run(){
        HttpURLConnection con = null;
        try{
            URL url = new URL(URLNode.getURL());
            con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setConnectTimeout(connTimeout);
            con.setReadTimeout(readTimeout);
            RequestNode requestNode = new RequestNode();
            con.connect();
            URLNode.addRequestNode(requestNode.setResponse(con.getResponseMessage())
                                                .setResponseCode("" + con.getResponseCode())
                                                .setResponseTime());
        }
        catch(Exception e){
            URLNode.addRequestNode(new RequestNode(e.getMessage(),"-1"));
        }
        finally{
            if(con != null)
                con.disconnect();
        }
        return;
    }
    public boolean checkResponse(String responseCode){
        return this.URLNode.checkLastResponse(responseCode);
    }
    public JSONObject toJSON(){
        return URLNode.toJSON();
    }
}
