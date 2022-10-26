import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

public class RequestNode {
    private String response;
    private String responseCode;
    private long requestTime;
    private long responseTime;
    private static String dateFormat ="yy/MM/dd HH-mm-ss.SSS";


    public String getDateFormat() {
        return dateFormat;
    }


    public static void setDateFormat(String dateFormat) {
        RequestNode.dateFormat = dateFormat;
    }

    private static String getFormattedDate(Date date){
        SimpleDateFormat sDateFormat = new SimpleDateFormat(dateFormat); 
        String formattedDate = sDateFormat.format(date);
        return formattedDate;
    }

    public String getFormattedRequestTime(){
        return RequestNode.getFormattedDate(new Date(requestTime));
    }

    public String getFormattedResponseTime(){
        return RequestNode.getFormattedDate(new Date(responseTime));
    }

    public long getDelta(){
        return this.responseTime - this.requestTime;
    }

    public RequestNode setRequestTime(long requestTime){
        this.requestTime = requestTime;
        return this;
    }

    public RequestNode setRequestTime(){
        this.requestTime = System.currentTimeMillis();
        return this;
    }

    public RequestNode setResponseTime(long responseTime){
        this.responseTime = responseTime;
        return this;
    }

    public RequestNode setResponseTime(){
        this.responseTime = System.currentTimeMillis();
        return this;
    }

    public long getRequestTime(){
        return this.requestTime;
    }

    public long getResponseTime(){
        return this.responseTime;
    }

    public String getResponse() {
        return this.response;
    }
    public RequestNode setResponse(String response) {
        this.response = response;
        return this;
    }
    public String getResponseCode() {
        return this.responseCode;
    }
    public RequestNode setResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public JSONObject toJSON(){
        JSONObject root = new JSONObject().put("responseCode", responseCode)
                                            .put("response", response)
                                            .put("requestTime", getFormattedRequestTime())
                                            .put("responseTime", getFormattedResponseTime())
                                            .put("delta", "" + getDelta() + "ms");
        return root;
    }

    public RequestNode(String response, String responseCode){
        this.response = response;
        this.responseCode = responseCode;
        this.setRequestTime();
        this.setResponseTime();
    }

    public RequestNode(){
        this.response = "";
        this.responseCode = "";
        this.setRequestTime();
        this.setResponseTime();
    }
}
