import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.JSONObject;

public class RequestNode {
    private String response;
    private String responseCode;
    private long requestDate;
    private long responseDate;
    private static String dateFormat ="yy/MM/dd HH-mm-ss.SSS";


    public String getDateFormat() {
        return dateFormat;
    }


    public static void setDateFormat(String dateFormat) {
        RequestNode.dateFormat = dateFormat;
    }

    private static String getFormattedDate(Date date){
        return new SimpleDateFormat(dateFormat).format(date);
    }

    public String getFormattedRequestDate(){
        return RequestNode.getFormattedDate(new Date(requestDate));
    }

    public String getFormattedResponseDate(){
        return RequestNode.getFormattedDate(new Date(responseDate));
    }

    public long getDelta(){
        return this.responseDate - this.requestDate;
    }

    public RequestNode setRequestDate(long requestDate){
        this.requestDate = requestDate;
        return this;
    }

    public RequestNode setRequestDate(){
        this.requestDate = System.currentTimeMillis();
        return this;
    }

    public RequestNode setResponseDate(long responseDate){
        this.responseDate = responseDate;
        return this;
    }

    public RequestNode setResponseDate(){
        this.responseDate = System.currentTimeMillis();
        return this;
    }

    public long getRequestDate(){
        return this.requestDate;
    }

    public long getResponseDate(){
        return this.responseDate;
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
                                            .put("requestDate", getFormattedRequestDate())
                                            .put("responseDate", getFormattedResponseDate())
                                            .put("responseTime", "" + getDelta() + "ms");
        return root;
    }

    public RequestNode(String response, String responseCode){
        this.response = response;
        this.responseCode = responseCode;
        this.setRequestDate();
        this.setResponseDate();
    }

    public RequestNode(){
        this.response = "";
        this.responseCode = "";
        this.setRequestDate();
        this.setResponseDate();
    }
}
