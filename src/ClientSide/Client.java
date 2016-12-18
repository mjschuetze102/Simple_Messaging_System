package ClientSide;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;
import Message.Message;
import jdk.internal.util.xml.impl.Input;

/**
 * The model for the client
 * Created by Michael on 12/10/2016.
 * Updated by Michael on 12/14/2016.
 * TODO: setClientName
 */
public class Client extends Observable {

    private InputManager inputManager;
    private ObjectOutputStream output;
    private Socket connection;

    /** Name of the host IP address */
    final String HOST = "localhost";

    /** Number of the port connecting to */
    final int PORT = 9001;

    /** The name of the client */
    private String clientName = "mjschuetze";

    /** The group of users using the messaging system */
    private ArrayList<String> users;

    /** The group of users selected by the GUI */
    private ArrayList<String> selectedUsers;

    /** The group of recipients in the last whisper */
    private ArrayList<String> whisperGroup;

    /** The new message being read by system */
    private Message message;

    /**
     *  Constructor class for Client model
     */
    public Client(){
        // Initiate the variables
        this.users= new ArrayList<>();
        this.selectedUsers= new ArrayList<>();
        this.whisperGroup= new ArrayList<>();
        this.message= null;
    }

    /////////////////////////////////////////////////////////////
    //  Client Connection Functions
    /////////////////////////////////////////////////////////////

    /**
     * Run all the functions requires to start the connection to server
     */
    public void startRunning(){
        try {
            // Connect a new socket
            connectToServer();
            // Set up the input and output streams for the socket
            setupStreams();
            // Tell the server a new user has joined
            onStart();
        } catch (EOFException EOFex) {
            // Tell the GUI that the connection was terminated
            receiveMessage(new Message("Client", new ArrayList<>(), "Connection terminated."));
            closeClient();
        } catch (IOException IOex) {
            IOex.printStackTrace();
            closeClient();
        }
    }

    /**
     * Connects to the server and creates a socket
     */
    private void connectToServer() throws IOException{
        // Tell the GUI that it is searching for a connection
        receiveMessage(new Message("Client", new ArrayList<>(), "Searching for connection."));

        connection = new Socket(InetAddress.getByName(HOST), PORT);

        // Tell the GUI that a connection was found
        receiveMessage(new Message("Client", new ArrayList<>(), "Connected to: "+ connection.getInetAddress().getHostName()+ ":"+
                                                                connection.getInetAddress().getHostAddress()));
    }

    /**
     * Sets up the clients input and output streams
     */
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();

        // Create the thread that will receive messages from the server
        inputManager= new InputManager(this, connection);
        inputManager.start();

        // Tell the GUI that the streams have been set up
        receiveMessage(new Message("Client", new ArrayList<>(), "Streams have been set up."));
    }

    /**
     * Closes the client and all of its streams and socket
     */
    private void closeClient(){
        // Tell the GUI that the connection is being closed
        receiveMessage(new Message("Client", new ArrayList<>(), "Closing connection."));

        try{
            output.close();
            inputManager.close();
            connection.close();
        }catch (IOException IOex){
            System.err.print("\nIOex Client-closeClient: " + IOex.getMessage());
        }
    }

    /////////////////////////////////////////////////////////////
    //  Client Interactions with GUI
    /////////////////////////////////////////////////////////////

    /**
     * Called when program opens to tell server user has joined
     */
    public void onStart(){
        // Get the client name
        String clientName= this.clientName;

        // Create a new special case message for server to interpret
        Message message= new Message(clientName, new ArrayList<>(), "");

        // Call the sendMessage function with the message to send
        sendMessage(message);
    }

    /**
     * Called when program closes to tell server user has left
     */
    public void onClose(){
        // Get the values of each of the fields
        String clientName= this.clientName;
        ArrayList<String> recipients= new ArrayList<>(selectedUsers);

        // Create a new special case message for server to interpret
        Message message= new Message(clientName, recipients, "");

        // Call the sendMessage function with the message to send
        sendMessage(message);

        // Close the client's connection
        closeClient();
    }

    /**
     * Changes the list of selected users
     * @param user- user being added to the list of selected users
     */
    public void changeSelectedUsers(String user){
        // Get the current selected user list
        ArrayList<String> selectedUsers= this.selectedUsers;

        // If the user is on the list, remove them
        // Else, add them to the list
        if(selectedUsers.contains(user))
            selectedUsers.remove(user);
        else
            selectedUsers.add(user);

        setSelectedUsers(selectedUsers);

        // Notify the observer of the changes
        setChanged();
        notifyObservers();
        resetToDefault();
    }

    /////////////////////////////////////////////////////////////
    //  Client Interactions with Server
    /////////////////////////////////////////////////////////////

    /**
     * Gets a message from the server and interprets it in one of two ways
     *      1. If text contains a value, displays the message for the user to see
     *      2. If text is "" and sender is null, update the user list
     * @param message- Message object from server
     */
    public void receiveMessage(Message message){
        String text= message.getMessage();
        String sender= message.getSender();
        ArrayList<String> recipients= message.getReceivers();

        // If text contains a value, store the whisper recipients, and display the message
        // Else check for the special server-client interactions
        if(!text.equals("")) {
            if(recipients.size() != users.size())
                setWhisperGroup(recipients);

            setMessage(message);
        } else {
            // If the sender is null, set the ListView
            if(sender == null){
                setUsers(recipients);
            }
        }

        // Notify the observer of the changes
        setChanged();
        notifyObservers();
        resetToDefault();
    }

    /**
     * Sends a message to the server for interpretation
     * @param message- Message object containing sender, recipients, and text
     */
    public void sendMessage(Message message){
        try{
            output.writeObject(message);
            output.flush();
        }catch (IOException IOex){
            System.err.print("\nIOex Client-sendMessage: " + IOex.getMessage());

            // Tell the GUI that the the message couldn't send
            receiveMessage(new Message("Client", new ArrayList<>(), "Error: Could not send message."));

            // Notify the observer of the changes
            setChanged();
            notifyObservers();
            resetToDefault();
        }
    }

    /////////////////////////////////////////////////////////////
    //  Getters and Setters
    /////////////////////////////////////////////////////////////

    /**
     * Gets the name of the user using the messaging system
     * Is not called in GUI update method
     * @return String containing the name of the user
     */
    public String getClientName() {
        return this.clientName;
    }

    /**
     * Gets the list of users using the the messaging system
     * @return ArrayList containing the list of users
     */
    public ArrayList<String> getUsers() {
        return this.users;
    }

    /**
     * Sets the list of users using the the messaging system
     * @param recipients- the users that are using the messaging system
     */
    private void setUsers(ArrayList<String> recipients){
        this.users= recipients;
    }

    /**
     * Gets the list of users currently selected to private message
     * @return ArrayList containing the list of selected users
     */
    public ArrayList<String> getSelectedUsers(){
        return this.selectedUsers;
    }

    /**
     * Sets the list of users currently selected to private message
     * @param recipients- the users that are currently selected
     */
    private void setSelectedUsers(ArrayList<String> recipients){
        this.selectedUsers= recipients;
    }

    /**
     * Gets the last group of recipients sent in a whisper
     * Is not called in GUI update method
     * @return ArrayList containing the list of recipients from the whisper
     */
    public ArrayList<String> getWhisperGroup(){
        return this.whisperGroup;
    }

    /**
     * Sets the last group of recipients sent in a whisper
     * @param recipients- the users the message was sent to
     */
    private void setWhisperGroup(ArrayList<String> recipients){
        this.whisperGroup= recipients;
    }

    /**
     * Gets the message that the server sent to the client
     * @return Message containing the message being sent
     */
    public Message getMessage(){
        return this.message;
    }

    /**
     * Sets the last message that was sent to the user
     * @param message- the message the server sent
     */
    private void setMessage(Message message){
        this.message= message;
    }

    /**
     * Resets variables to null to prevent them from being displayed twice
     */
    private void resetToDefault(){
        this.message= null;
    }
}
