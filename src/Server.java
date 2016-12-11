/**
 *
 * Created by Michael on 12/10/2016.
 */

import java.io.*;
import java.net.*;

/**
 * Runs the server
 */
public class Server {

    // Predefined variables
    final int PORT = 9001;
    final int MAXUSERS = 10;

    // Undefined variables
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Constructor for the Server class
     */
    public Server() {
    }

    public void run() {
        // Run the server catching any IO exceptions
        try {
            // Create a server socket
            serverSocket = new ServerSocket(PORT, MAXUSERS);

            while (true) {
                // Connect to the serverSocket
                socket = serverSocket.accept();

                // Create a new thread for the connection and start it
                ClientThread thread = new ClientThread(socket);
                thread.start();
            }
        } catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
