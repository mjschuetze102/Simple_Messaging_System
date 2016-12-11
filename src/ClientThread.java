/**
 *
 * Created by Michael on 12/10/2016.
 */

import java.net.Socket;

public class ClientThread {

    Socket socket;

    /**
     * Constructor for the ClientThread class
     * @param socket- the accepted ServerSocket the system will be running on
     */
    public ClientThread(Socket socket){
        this.socket = socket;
    }

    public static void start(){}
}