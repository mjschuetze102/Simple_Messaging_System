import javax.swing.*;

/**
 * Created by Oscar on 12/10/2016.
 */
public class TestClient {
    public static void main(String[] args){
        Client charlie = new Client("127.0.0.1");
        charlie.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        charlie.startRunning();
    }
}
