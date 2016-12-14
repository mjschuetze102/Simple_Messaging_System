/**
 * Created by Oscar on 12/13/2016.
 */

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class OutputManager {

    static HashMap<String, ObjectOutputStream> outputs = new HashMap<>();

    static synchronized void addOutput( String client, ObjectOutputStream out ){
        outputs.put(client, out);
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

        for ( String recipient : m.getRecievers()){
            try{
                outputs.get(recipient).writeObject(m);
            }catch (IOException IOex){
                System.err.print("\nSending Message err: " + IOex.getMessage());
            }
        }

    }


}
