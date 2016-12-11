/**
 *
 * Created by Michael on 12/10/2016.
 */

import java.io.*;
import java.net.*;

public class ClientThread {

    // Predefined variables
    final int PORT = 9001;
    String host = "localhost";

    // Undefined variables
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    /**
     * Constructor for the ClientThread class
     * @param socket- the accepted ServerSocket the system will be running on
     */
    public ClientThread(Socket socket){
        this.socket = socket;
    }

    public void start(){
    }
}