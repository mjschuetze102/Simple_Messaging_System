package ServerSide;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.HashMap;
import Message.*;

/**
 * Created by Oscar on 12/13/2016.
 */
public class OutputManager {

    static HashMap<String, ObjectOutputStream> outputs = new HashMap<>();

    static synchronized void addOutput( String client, ObjectOutputStream out ){
        outputs.put(client, out);
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
            System.err.print("\nClosing output: " + IOex.getMessage());
        }
    }

    static synchronized void sendMessage( Message m ){
        for ( String recipient : m.getReceivers()){
            try{
                outputs.get(recipient).writeObject(m);
            }catch (IOException IOex){
                System.err.print("\nSending Message err: " + IOex.getMessage());
            }
        }
    }
}
