public class UrlNode {
    public String URL;
    public String response;
    public String responseCode;
    public UrlNode(String URL, String response, String responseCode){
        this.URL = URL;
        this.response = response;
        this.responseCode = responseCode;
    }
    public UrlNode(String URL, String response){
        this.URL = URL;
        this.response = response;
        this.responseCode = "";
    }
    public UrlNode(String URL){
        this.URL = URL;
        this.response = "";
        this.responseCode = "";
    }
    public String getFullResponse(){
        return this.responseCode + " - " + this.response;
    }
}
