package ClientSide;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * The model for the client
 * Created by Michael on 12/10/2016.
 * Updated by Michael on 12/14/2016.
 * TODO: setClientName
 */
public class Client extends Observable {

    private ObjectOutputStream output;
    private Socket connection;

    /** Name of the host IP address */
    final String HOST = "localhost";

    /** The name of the client */
    private String clientName = "Client";

    /** The group of users using the messaging system */
    private ArrayList<String> users;

    /** The group of users selected by the GUI */
    private ArrayList<String> selectedUsers;

    /** The group of recipients in the last whisper */
    private ArrayList<String> whisperGroup;

    /** The new message being read by system */
    private Message message;

    /** The error that has occurred */
    private String error;

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
     * Connect to the server
     */
    public void startRunning(){
        try {
            connectToServer();
            setupStreams();
            // Tell the server a new user has joined
            onStart();
        } catch (EOFException EOFex) {
            setError("Client terminated connection");
        } catch (IOException IOex) {
            IOex.printStackTrace();
        } finally {
            closeClient();
        }
    }

    /**
     * Connect to server
     */
    private void connectToServer() throws IOException{
        setError("Attempting connection..");
        connection = new Socket(InetAddress.getByName(HOST), 9001);
        setError("Connected to:" + connection.getInetAddress().getHostName());
    }

    /**
     * Set up in out streams
     */
    private void setupStreams() throws IOException{
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        InputManager inputManager= new InputManager(this, connection);
        setError("Streams are set up...");
    }

    /**
     * Closing all the streams and sockets in the client
     */
    private void closeClient(){
        setError("Closing the client...");
        try{
            output.close();
            connection.close();
        }catch (IOException IOex){
            IOex.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////
    //  Helper Functions
    /////////////////////////////////////////////////////////////

    /**
     * Resets variables to null to prevent them from being displayed twice
     */
    private void resetToDefault(){
        this.message= null;
        this.error= null;
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
    //  Client Interactions with ServerSide.Server
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
            setError("Error sending message...");

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
     * Gets the error message to be displayed to the user
     * @return String containing the error message
     */
    public String getError() { return this.error; }

    /**
     * Sets the error message to be displayed to the user
     * @param error- the error to be displayed
     */
    private void setError(String error) { this.error= error; }
}
