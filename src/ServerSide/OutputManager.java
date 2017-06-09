package ServerSide;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.HashMap;
import Message.*;

/**
 * Created by Oscar on 12/13/2016.
 * Updated by Michael on 12/20/2016.
 *      Added changeOutput
 */
public class OutputManager {

    static HashMap<String, ObjectOutputStream> outputs = new HashMap<>();

    static synchronized void addOutput( String client, ObjectOutputStream out ){
        outputs.put(client, out);
    }

    static synchronized void changeOutput(String client, String newName){
        // Get the output stream associated with the client
        ObjectOutputStream out= outputs.get(client);

        // Create the new key/value pair and remove the old one
        outputs.put(newName, out);
        outputs.remove(client);
    }

    static synchronized ArrayList<String> getClientList(){
        Set<String> keys = outputs.keySet();

        ArrayList<String> clientList = new ArrayList<>();
        for ( String client : keys ){
            clientList.add(client);
        }

        return clientList;
    }

    static synchronized void removeOutput( String client ){
        try{
            outputs.get(client).close();
            outputs.remove(client);
        }catch (IOException IOex){
            System.err.print("Closing output: " + IOex.getMessage());
        }
    }

    static synchronized void sendMessage( Message m ){
        for ( String recipient : m.getReceivers()){
            try{
                outputs.get(recipient).writeObject(m);
                outputs.get(recipient).flush();
            }catch (IOException IOex){
                System.err.print("Sending Message err: " + IOex.getMessage());
            }
        }
    }
}
