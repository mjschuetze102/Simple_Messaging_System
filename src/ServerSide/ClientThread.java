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

                Message m = (Message) in.readObject();
                if ( !m.getMessage().equals("") ){
                    System.err.print("Faulty Identifier Message!! Possible false name.");
                }else {
                    clientName= m.getSender();
                    OutputManager.addOutput(m.getSender(), out);

                    // Display that the Client has been added to the ClientList
                    System.out.println("\nClient has been added to the server");
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
                System.out.println("\nClientList has been sent");

                do {

                    m = (Message) in.readObject();

                    // Display that the message has been read
                    System.out.println("\nMessage: "+ m.toString()+ " has been read.");

                    OutputManager.sendMessage(m);

                    // Display that the message has been sent out
                    System.out.println("\nMessage: "+ m.toString()+ " has been sent.");

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
                System.out.println("\nStreams and socket have been closed");
            }

        }catch (IOException IOex){
            System.err.println("Server: " + IOex.getMessage());
        }
    }
}