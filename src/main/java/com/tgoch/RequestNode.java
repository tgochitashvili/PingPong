package com.tgoch;
import java.util.Date;

import com.json.JSONObject;

import java.text.SimpleDateFormat;

public class RequestNode {
    private static String successCode = "200";
    private String response;
    private String responseCode;
    private long requestDate;
    private long responseDate;
    private static String dateFormat ="yy/MM/dd HH-mm-ss.SSS";

    private static boolean lightlog = false;

    public static void setLogType(boolean lightlog){
        RequestNode.lightlog = lightlog;
    }

    public static boolean getLogType(){
        return RequestNode.lightlog;
    }

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

    public boolean isSuccessful(String successCode){
        return this.responseCode.equals(successCode);
    }

    public String getResponseCode() {
        return this.responseCode;
    }
    public RequestNode setResponseCode(String responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    public JSONObject toJSON(){

        return lightlog?
                new JSONObject().put("response", this.getFormattedResponse()):

                new JSONObject().put("responseCode", responseCode)
                        .put("response", response)
                        .put("requestDate", getFormattedRequestDate())
                        .put("responseDate", getFormattedResponseDate())
                        .put("responseTime", "" + getDelta() + "ms");
    }

    public boolean getSuccess(){
        return this.responseCode.equals("") || this.responseCode.equals(RequestNode.successCode);
    }

    public static void setRequestSuccessCode(String successCode){
        RequestNode.successCode = successCode;
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
    public String getFormattedResponse(){
        return getResponseCode() + " - " + getResponse();
    }
}
