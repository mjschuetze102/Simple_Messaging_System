import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

/**
 * Runs the server for the project
 * Created by Michael on 12/10/2016.
 */
public class Server extends JFrame {

    // Predefined variables
    final int PORT= 9001;
    final int MAXUSERS= 10;

    // Undefined variables
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;

    // GUI Variables
    private JTextField userText;
    private JTextArea chatWindow;

    /**
     * Constructor for the Server class
     */
    public Server() {
        super("Simple Messaging System");
        userText= new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
            new ActionListener(){
                public void actionPerformed(ActionEvent event){
                    sendMessage(event.getActionCommand());
                    userText.setText("");
                }
            }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow= new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300, 150);
        setVisible(true);
    }

    /**
     * Runs the server allowing for users to connect
     */
    public void run() {
        // Run the server catching any IO exceptions
        try {
            // Create a server socket
            serverSocket= new ServerSocket(PORT, MAXUSERS);

            while (true) {
                try {
                    waitForConnection();
                    setupSteams();
                    allowChat();
                } catch(EOFException eof) {
                    showMessage("\n Server Connection Ended");
                } finally {
                    closeConnection();
                }
            }
        } catch(IOException io){
            io.printStackTrace();
        }
    }

    /**
     * Waits for a connection
     * Once connected, displays Connected message
     * @throws IOException
     */
    private void waitForConnection() throws IOException {
        // Displays a message for the user
        showMessage("Waiting for someone to connect...\n");

        // Creates a connection between the server and client
        socket = serverSocket.accept();

        // Displays a message for the user
        showMessage("Now connected to "+ socket.getInetAddress().getHostName());
    }

    /**
     * Sets up the send/receive streams
     * @throws IOException
     */
    private void setupSteams() throws IOException {
        // Sets up the output stream to send data and flushes out data
        outputStream= new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();

        // Sets up the input stream to collect data
        inputStream= new ObjectInputStream(socket.getInputStream());

        // Displays a message for the user
        showMessage("\nStreams are now setup!\n");
    }

    /**
     * Allows clients to send messages back and forth
     * @throws IOException
     */
    private void allowChat() throws IOException {
        String message = "You are now connected!";
        sendMessage(message);
        ableToType(true);
        do {
            try {
                message = inputStream.readObject().toString();
                showMessage("\n"+ message);
            } catch(ClassNotFoundException cnf) {
                showMessage("\nUser has not sent appropriate data");
            }
        } while(!message.equals("CLIENT - END"));
    }

    /**
     * Closes the connection between two clients
     */
    private void closeConnection() {
        showMessage("\nClosing connection... \n");
        ableToType(false);
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch(IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Sends message to the client
     * @param message- string for message being sent to client
     */
    private void sendMessage(String message){
        try {
            outputStream.writeObject("SERVER - "+ message);
            outputStream.flush();
            showMessage("\nSERVER - "+ message);
        } catch(IOException io) {
            chatWindow.append("\nERROR: Can't Send Message");
        }
    }

    /**
     * Updates the chat window for the client
     * @param message- string for message to be displayed
     */
    private void showMessage(final String message){
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    chatWindow.append(message);
                }
            }
        );
    }

    /**
     * Allows user to type in their textField
     * @param bool- boolean value
     */
    private void ableToType(final boolean bool){
        SwingUtilities.invokeLater(
            new Runnable() {
                @Override
                public void run() {
                    userText.setEditable(bool);
                }
            }
        );
    }
}
