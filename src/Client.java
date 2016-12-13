/**
 * Created by Oscar on 12/10/2016.
 */

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{

    private JTextField userText;
    private JTextArea chatWindow;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message = "";
    private String serverIP;
    private Socket connection;

    /**
     *  Constructer
     */
    public Client(String host){

        super("Client");
        serverIP = host;
        userText = new JTextField();
        userText.setEditable(false);
        userText.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        sendData(e.getActionCommand());
                        userText.setText("");
                    }
                }
        );

        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300,150);
        setVisible(true);

    }

    /**
     * Connect to the server
     */
    public void startRunning(){
        try{
            connectToServer();
            setupStreams();
            whileChatting();

        }catch(EOFException EOFex){
            showMessage("\n Client terminated connection");
        }catch(IOException IOex){
            IOex.printStackTrace();
        }finally {
            closeClient();
        }
    }

    /**
     * Connect to server
     */
    private void connectToServer() throws IOException{
        showMessage("Attempting connection.. \n");
        connection = new Socket(InetAddress.getByName(serverIP), 9001);
        showMessage("Connected to:" + connection.getInetAddress().getHostName());
    }

    /**
     * Set up in out streams
     */
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        showMessage("\n Streams are set up... \n");
    }

    /**
     * This runs while chatting with the server...
     */
    private void whileChatting() throws IOException{
        ableToType(true);
        do{
            try{
                message = (String) input.readObject();
                showMessage("\n" + message);
            }catch (ClassNotFoundException ClsLost){
                showMessage("\n Unknown Object Type.");
            }
        }while(!message.equals("SERVER - END"));
    }

    /**
     * Closing all the streams and sockets in the client
     */
    private void closeClient(){
        showMessage("\nClosing the client...\n");
        ableToType(false);
        try{
            output.close();
            input.close();
            connection.close();
        }catch (IOException IOex){
            IOex.printStackTrace();
        }
    }

    /**
     * Sends messages to the server
     *
     * String Message - the message that gets passed to the server
     */
    private void sendData(String message){
        try{
            output.writeObject("CLIENT - " + message);
            output.flush();
            showMessage("\nCLIENT - " + message);
        }catch (IOException IOex){
            chatWindow.append("\nError sending message...");
        }
    }

    /**
     * Show Message in the chat window
     */
    private void showMessage(final String m){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        chatWindow.append(m);
                    }
                }
        );
    }

    /**
     * Allows permission to type into chat box
     */
    private void ableToType(final boolean tof){
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                        userText.setEditable(tof);
                    }
                }
        );
    }

}
