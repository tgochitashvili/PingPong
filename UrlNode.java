import java.util.LinkedList;

public class UrlNode {
    private String URL;
    private LinkedList<RequestNode> requestNodeList;


    public RequestNode getLastRequestNode(){
        try{
            return this.requestNodeList.getLast();
        }
        catch(Exception e){
            return new RequestNode();
        }
    }

    public UrlNode addRequestNode(RequestNode requestNode){
        this.requestNodeList.add(requestNode);
        return this;
    }

    public UrlNode setRequestNodeList(LinkedList<RequestNode> requestNodeList){
        this.requestNodeList = requestNodeList;
        return this;
    }

    public UrlNode resetRequestNodeList(){
        this.setRequestNodeList(new LinkedList<RequestNode>());
        return this;
    }
   
    public String getURL() {
        return this.URL;
    }
    public UrlNode setURL(String URL) {
        this.URL = URL;
        return this;
    }

    public UrlNode(String URL, LinkedList<RequestNode> requestNodeList){
        this.URL = URL;
        this.setRequestNodeList(requestNodeList);
    }
    public UrlNode(String URL, RequestNode requestNode){
        this.URL = URL;
        this.resetRequestNodeList().addRequestNode(requestNode);
    }
    public UrlNode(String URL){
        this.URL = URL;
        this.resetRequestNodeList();
    }
    public String getLastFormattedResponse(){
        return this.getLastRequestNode().getResponseCode() + " - " + this.getLastRequestNode().getResponse();
    }
    public boolean checkLastResponse(String responseCode){
        return this.getLastRequestNode().getResponseCode().equals(responseCode);
    }
}
