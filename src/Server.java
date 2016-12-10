import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * Created by Michael on 12/10/2016.
 */
public class Server {
    public static void main(String[] args) {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(9001);
        } catch(IOException ex){
            System.err.println(ex);
        }
    }
}
