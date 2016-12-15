package ServerSide;

import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * Runs the server for the project
 * Created by Michael on 12/10/2016.
 * Updated by Oscar on 12/14/2016.
 */
public class Server extends JFrame {

    // Predefined variables
    final int PORT = 9001;
    final int MAXUSERS = 10;

    // Undefined variables
    private ServerSocket serverSocket;
    private Socket socket;

    /**
     * Runs the server allowing for users to connect
     */
    public void run() {
        // Run the server catching any IO exceptions
        try {
            // Create a server socket
            serverSocket = new ServerSocket(PORT, MAXUSERS);

            while (true) {
                try {
                    waitForConnection();
                    setupThread();
                } catch (EOFException eof) {
                    System.err.print("\n ServerSide.Server Connection Ended");
                }
            }
        } catch(IOException io){
            io.printStackTrace();
        }
    }

    /**
     * Waits for a connection
     * @throws IOException
     */
    private void waitForConnection() throws IOException {
        socket = serverSocket.accept();
    }

    /**
     * Sets up the send/receive streams
     * @throws IOException
     */
    private void setupThread() throws IOException {
        // Sets up the output stream to send data and flushes out data
        ClientThread thread = new ClientThread(socket);
        thread.start();
    }
}