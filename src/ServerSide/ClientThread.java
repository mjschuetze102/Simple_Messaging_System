package ServerSide;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import Message.*;

/**
 *
 * Created by Michael on 12/10/2016.
 * Updated by Oscar on 12/12/2016.
 * Updated by Michael on 12/15/2016.
 * Updated by Michael on 12/20/2016.
 *      Added functionality in run(), added checkNameChange()
 */
public class ClientThread extends Thread{

    // Predefined variables
    String clientName = "";

    // Undefined variables
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;

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
            out = new ObjectOutputStream(socket.getOutputStream());

            try {
                // Receive the name of the client
                // Message should be clientName, new ArrayList(), ""
                Message m = (Message) in.readObject();
                if ( !m.getMessage().equals("") ){
                    System.err.print("Faulty Identifier Message!! Possible false name.");
                }else {
                    // Add the client to the ClientList
                    clientName= m.getSender();
                    OutputManager.addOutput(m.getSender(), out);

                    // Display that the Client has been added to the ClientList
                    System.out.println("Client has been added to the server");
                }

                // Send a message to the client saying the connection has been established
                ArrayList<String> client= new ArrayList<>(); client.add(clientName);
                m= new Message("Server", client, "Connection has been established.");
                OutputManager.sendMessage(m);

                // Send the new client list to all the clients
                m = new Message( null, OutputManager.getClientList(), "" );
                OutputManager.sendMessage( m );

                // Send message saying a new client has joined
                m = new Message("Server", OutputManager.getClientList(), clientName+ " has joined the chat.");
                OutputManager.sendMessage(m);

                // Display that the clientList has been sent
                System.out.println("ClientList has been sent");

                do {
                    // Read in the message being sent
                    m = (Message) in.readObject();

                    // Display that the message has been read
                    System.out.println("Message: "+ m.toString()+ " has been read.");

                    // If client is not changing their name, display the message
                    if(!checkNameChange(m)){
                        // Send the message
                        OutputManager.sendMessage(m);

                        // Display that the message has been sent out
                        System.out.println("Message: "+ m.toString()+ " has been sent.");
                    }
                }while (true);

            }catch (ClassNotFoundException NFex){
                System.err.println("Getting Message err: " + NFex.getMessage());
            }catch (EOFException eof){
                in.close();
                OutputManager.removeOutput(clientName);
                socket.close();

                Message m;

                // Send the new client list to all the clients
                m = new Message( null, OutputManager.getClientList(), "" );
                OutputManager.sendMessage( m );

                // Send message saying a client has left
                m = new Message("Server", OutputManager.getClientList(), clientName+ " has left the chat.");
                OutputManager.sendMessage(m);

                // Display that the socket and streams has been closed
                System.out.println("Streams and socket have been closed");
            }

        }catch (IOException IOex){
            System.err.println("Server: " + IOex.getMessage());
        }
    }

    /**
     * Interprets the message to see if the client changed their name
     * @param m- Message sent by the Client
     */
    private boolean checkNameChange(Message m){
        // If condition to change client name has occurred
        // Message should be clientName, new ArrayList(), newName
        // Receivers.size only 0 for name change and closing client
        // Message is only "" for closing client
        if(m.getReceivers().size() == 0 && !m.getMessage().equals("")){
            // Get the new and old client names
            String oldUsername= m.getSender(); String newUsername= m.getMessage();

            // Change the key/value pair for the ClientList
            OutputManager.changeOutput(oldUsername, newUsername);

            // Change the client's name to the new one
            clientName= newUsername;

            // Display that the client has changed their name
            System.out.println("Client: "+ clientName+ " name change completed.");

            // Send the new client list to all the clients
            m = new Message( null, OutputManager.getClientList(), "" );
            OutputManager.sendMessage( m );

            // Send message saying a client has changed their name
            m = new Message("Server", OutputManager.getClientList(), oldUsername+ " has changed their name to: "+ newUsername+ ".");
            OutputManager.sendMessage(m);

            return true;
        }

        return false;
    }
}