import java.util.*;

/**
 * Created by Oscar on 12/10/2016.
 */
public class Message {

    private String sender;
    private ArrayList<String> recievers = new ArrayList<String>();
    private String message = "";

    public Message(String sender, ArrayList<String> recievers, String message){

        this.sender = sender;
        this.recievers = recievers;
        this.message = message;

    }
    
    public String getSender(){return this.sender;}
    public ArrayList<String> getRecievers(){return this.recievers;}
    public String getMessage(){return this.message;}


}
