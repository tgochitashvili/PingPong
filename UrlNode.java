public class UrlNode {
    public String URL;
    public String response;
    // public boolean isTaken;
    public boolean isChecked;
    public UrlNode(String URL, String response, boolean isTaken, boolean isChecked){
        this.URL = URL;
        this.response = response;
        // this.isTaken = isTaken;
        this.isChecked = isChecked;
    }
    public UrlNode(String URL, String response){
        this.URL = URL;
        this.response = response;
        // this.isTaken = false;
        this.isChecked = false;
    }
    public UrlNode(String URL){
        this.URL = URL;
        // this.isTaken = false;
        this.isChecked = false;
    }
}
