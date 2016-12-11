/**
 *
 * Created by Michael on 12/10/2016.
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientThread {

    // Predefined variables
    final int PORT = 9001;
    String host = "localhost";

    // Undefined variables
    Socket socket;
    ObjectInputStream in;
    DataOutputStream out;

    /**
     * Constructor for the ClientThread class
     * @param socket- the accepted ServerSocket the system will be running on
     */
    public ClientThread(Socket socket){
        this.socket = socket;
    }

    public void run(){

        try {
            in = new ObjectInputStream(socket.getInputStream());

            do {

                Object m = in.readObject();

                sendMessage(m);

            } while (true);
        }catch (IOException IOex){
            System.err.println("Server: " + ex.getMessage());
        }


        return;

    }
}