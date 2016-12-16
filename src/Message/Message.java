package Message;

import java.io.Serializable;
import java.util.*;

/**
 * Message class which will allow server and clients to talk with each other
 * Created by Oscar on 12/10/2016.
 * Updated by Michael on 12/12/2016.
 *
 * Tricks to Message Class:
 *      1. Users can not send blank messages
 *      2. If message is blank, a special case has occurred
 */
public class Message implements Serializable {

    private String sender;
    private ArrayList<String> receivers = new ArrayList<>();
    private String message = "";

    public Message(String sender, ArrayList<String> receivers, String message){
        this.sender = sender;
        this.receivers = receivers;
        this.message = message;
    }

    public String getSender(){return this.sender;}
    public ArrayList<String> getReceivers(){return this.receivers;}
    public String getMessage(){return this.message;}

    @Override
    public String toString() {
        return getMessage();
    }
}
