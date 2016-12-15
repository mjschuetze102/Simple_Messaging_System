package ServerSide;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

/**
 * Runs the server for the project
 * Created by Michael on 12/10/2016.
 */
public class Server extends JFrame {

    // Predefined variables
    final int PORT = 9001;
    final int MAXUSERS = 10;

    // Undefined variables
    private ServerSocket serverSocket;
    private Socket socket;

    // GUI Variables
    private JTextField userText;
    private JTextArea chatWindow;

    /**
     * Constructor for the ServerSide.Server class
     */
<<<<<<< HEAD
//    public ServerSide.Server() {
//        super("Simple Messaging System");
//        userText = new JTextField();
//        userText.setEditable(false);
//        userText.addActionListener(
//                new ActionListener() {
//                    public void actionPerformed(ActionEvent event) {
//                        sendMessage(event.getActionCommand());
//                        userText.setText("");
//                    }
//                }
//        );
//        add(userText, BorderLayout.NORTH);
//        chatWindow = new JTextArea();
//        add(new JScrollPane(chatWindow));
//        setSize(300, 150);
//        setVisible(true);
//    }
=======
    public Server() {
        super("Simple Messaging System");
        userText= new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
            event -> {
                    sendMessage(new Message("ServerSide.Server", new ArrayList<>(), userText.getText()));
                    userText.setText("");
            }
        );
        add(userText, BorderLayout.NORTH);
        chatWindow= new JTextArea();
        add(new JScrollPane(chatWindow));
        setSize(300, 150);
        setVisible(true);
    }
>>>>>>> refs/remotes/origin/master

    /**
     * Runs the server allowing for users to connect
     */
    public void run() {
        // Run the server catching any IO exceptions
        try {
            // Create a server socket
            serverSocket = new ServerSocket(PORT, MAXUSERS);

            while (true) {
                try {
                    waitForConnection();
<<<<<<< HEAD
                    setupThread();
                } catch (EOFException eof) {
                    System.err.print("\n ServerSide.Server Connection Ended");
=======
                    setupSteams();
                    allowChat();
                } catch(EOFException eof) {
                    showMessage("\nServerSide.Server Connection Ended");
                } finally {
                    closeConnection();
>>>>>>> refs/remotes/origin/master
                }

            }
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Waits for a connection
     * Once connected, displays Connected message
     *
     * @throws IOException
     */
    private void waitForConnection() throws IOException {
        // Displays a message for the user
        //showMessage("Waiting for someone to connect...\n");

        // Creates a connection between the server and client
        socket = serverSocket.accept();

        // Displays a message for the user
        //showMessage("Now connected to "+ socket.getInetAddress().getHostName());
    }

    /**
     * Sets up the send/receive streams
     *
     * @throws IOException
     */
    private void setupThread() throws IOException {
        // Sets up the output stream to send data and flushes out data
        ClientThread thread = new ClientThread(socket);
        thread.start();

        // Sets up the input stream to collect data

        // Displays a message for the user
<<<<<<< HEAD
        //showMessage("\nStreams are now setup!\n");
    }

=======
        showMessage("\nStreams are now setup!\n");
    }

    /**
     * Allows clients to send messages back and forth
     * @throws IOException
     */
    private void allowChat() throws IOException {
        Message message = new Message("ServerSide.Server", new ArrayList<>(), "You are now connected!");
        sendMessage(message);
        ableToType(true);
        do {
            try {
                message = (Message) inputStream.readObject();
                showMessage("\n"+ message.getMessage() +" XD");
            } catch(ClassNotFoundException cnf) {
                showMessage("\nUser has not sent appropriate data");
            }
        } while(message != null && !message.getMessage().equals("END"));
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
    private void sendMessage(Message message){
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            showMessage("\nSERVER - "+ message);
        } catch(IOException io) {
            chatWindow.append("\nERROR: Can't Send ServerSide.Message");
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
>>>>>>> refs/remotes/origin/master
}

//    /**
//     * Allows clients to send messages back and forth
//     * @throws IOException
//     */
//    private void allowChat() throws IOException {
//        String message = "You are now connected!";
//        sendMessage(message);
//        ableToType(true);
//        do {
//            try {
//                message = inputStream.readObject().toString();
//                showMessage("\n"+ message);
//            } catch(ClassNotFoundException cnf) {
//                showMessage("\nUser has not sent appropriate data");
//            }
//        } while(!message.equals("CLIENT - END"));
//    }
//
//    /**
//     * Closes the connection between two clients
//     */
//    private void closeConnection() {
//        showMessage("\nClosing connection... \n");
//        ableToType(false);
//        try {
//            outputStream.close();
//            inputStream.close();
//            socket.close();
//        } catch(IOException io) {
//            io.printStackTrace();
//        }
//    }
//
//    /**
//     * Sends message to the client
//     * @param message- string for message being sent to client
//     */
//    private void sendMessage(String message){
//        try {
//            outputStream.writeObject("SERVER - "+ message);
//            outputStream.flush();
//            showMessage("\nSERVER - "+ message);
//        } catch(IOException io) {
//            chatWindow.append("\nERROR: Can't Send ServerSide.Message");
//        }
//    }
//
//    /**
//     * Updates the chat window for the client
//     * @param message- string for message to be displayed
//     */
//    private void showMessage(final String message){
//        SwingUtilities.invokeLater(
//            new Runnable() {
//                @Override
//                public void run() {
//                    chatWindow.append(message);
//                }
//            }
//        );
//    }
//
//    /**
//     * Allows user to type in their textField
//     * @param bool- boolean value
//     */
//    private void ableToType(final boolean bool){
//        SwingUtilities.invokeLater(
//            new Runnable() {
//                @Override
//                public void run() {
//                    userText.setEditable(bool);
//                }
//            }
//        );
//    }
//}
