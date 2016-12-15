package ClientSide; /**
 * Created by Oscar on 12/14/2016.
 */

import ServerSide.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class InputManager extends Thread{

    private ObjectInputStream in;

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
    public InputManager(Socket socket){

        try{
            in = new ObjectInputStream(socket.getInputStream());
        }catch (IOException IOex){
            System.err.print("\nOpening inconnection: " + IOex.getMessage());
        }

    }

    @Override
    public void run() {

        while(true){
            try{
                Message m = (Message) in.readObject();

                /**
                 * TODO
                 * Add here what to do with the message that was just recieved.
                 * Have it call other class functions to update the GUI
                 */

            }catch (IOException IOex){
                System.err.print("\nRetreaving data from server: " + IOex.getMessage());
            }catch (ClassNotFoundException CLnotFound){
                System.err.print("\nRetreaving data from server: " + CLnotFound.getMessage());
            }
        }

    }
}
