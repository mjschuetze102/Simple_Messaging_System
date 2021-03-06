package ClientSide;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import javafx.application.Platform;
import Message.*;

/**
 * Reads the output from the server
 * Created by Oscar on 12/14/2016.
 * Updated by Michael on 12/15/2016.
 */
public class InputManager extends Thread {

    private ObjectInputStream input;

    private Client client;

    /**
     * Use this to manage the input for the client
     *
     * create a new inmanager when the client connects and pass it the socket.
     * Then call the .start message
     *
     * Next implement the TODO
     * explanation is below.
     *
     * @param socket - the socket that has been connected to in order
     */
    public InputManager(Client client, Socket socket){
        this.client= client;

        try{
            input = new ObjectInputStream(socket.getInputStream());
        }catch (IOException IOex){
            System.err.print("\nOpening inconnection: " + IOex.getMessage());
        }
    }

    /**
     * Reads messages from Server and displays it for Client
     */
    @Override
    public void run(){
        try {
            do {
                // Receive Message from the server
                Message message = (Message) input.readObject();

                // Allows for the UI to update while the client is still receiving data
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        client.receiveMessage(message);
                    }
                });

            }while (true);

        }catch (ClassNotFoundException NFex){
            System.err.println("Getting Message err: " + NFex.getMessage());
        }catch (EOFException eof){
            // When the server closes, close the client
            client.onClose();
        } catch (IOException io){
            System.err.println(io.toString());
        }
    }

    /**
     * Closes the input stream
     */
    public void close() {
        try{
            input.close();
        }catch (IOException IOex){
            System.err.print("\nOpening inconnection: " + IOex.getMessage());
            this.client.receiveMessage(new Message("Server", new ArrayList<>(), "Connection has been closed."+ IOex.getMessage()));
        }
    }
}
