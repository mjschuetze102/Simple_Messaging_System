package ClientSide;

import javafx.concurrent.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.util.ArrayList;
import Message.*;

/**
 * Reads the output from the server
 * Created by Oscar on 12/14/2016.
 * Updated by Michael on 12/15/2016.
 */
public class InputManager extends Service {

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

    protected Task<Message> createTask() {
        return new Task<Message>() {
            protected Message call()
                    throws IOException, ClassNotFoundException {
                Message result = null;
                try {
                    result= (Message) input.readObject();
                } finally {
                    if (input != null) {
                        input.close();
                    }
                }
                return result;
            }
        };
    }

    public void close() {
        try{
            input.close();
        }catch (IOException IOex){
            System.err.print("\nOpening inconnection: " + IOex.getMessage());
            this.client.receiveMessage(new Message("Server", new ArrayList<>(), "Connection has been closed."+ IOex.getMessage()));
        }
    }
}
