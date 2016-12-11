/**
 *
 * Created by Michael on 12/10/2016.
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Runs the server
 */
public class Server {
    public static void main(String[] args) {
        // Run the server catching any IO exceptions
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(9001);

            while (true) {
                Socket socket = serverSocket.accept();

                // Create a new thread for the connection and start it
                ClientThread thread = new ClientThread(socket);

                thread.start();
            }
        } catch(IOException ex){
            System.err.println(ex);
        }
    }
}
