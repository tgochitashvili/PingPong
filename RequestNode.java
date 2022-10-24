public class RequestNode {
    private String response;
    private String responseCode;
    private long requestTime;
    private long responseTime;

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
