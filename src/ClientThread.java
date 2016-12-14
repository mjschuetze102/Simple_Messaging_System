/**
 *
 * Created by Michael on 12/10/2016.
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientThread extends Thread{

    // Predefined variables
    final int PORT = 9001;
    String host = "localhost";
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
                    OutputManager.addOutput(m.getSender(), out);
                }

                do {

                    m = (Message) in.readObject();

                    OutputManager.sendMessage(m);

                }while (true);

            }catch (ClassNotFoundException NFex){
                System.err.println("Getting Message err: " + NFex.getMessage());
            }catch (EOFException eof){
                in.close();
                OutputManager.removeOutput(clientName);
                socket.close();
            }

        }catch (IOException IOex){
            System.err.println("Server: " + IOex.getMessage());
        }


        return;

    }
}