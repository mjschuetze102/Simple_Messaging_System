import javax.swing.JFrame;

/**
 *
 * Created by Michael on 12/10/2016.
 */
public class ServerTest {
    public static void main(String[] args){
        Server server = new Server();
        server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        server.run();
    }
}
