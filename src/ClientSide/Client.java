package ClientSide;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Observable;
import Message.Message;

/**
 * The model for the client
 * Created by Michael on 12/10/2016.
 * Updated by Michael on 12/14/2016.
 * Updated by Michael on 12/21/2016.
 *      Added startNameChange(), checkNameChange(), finishNameChange(), and changeName
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

    /** Boolean value to see if name is being changed */
    private boolean changeName;

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

        // Prevent users from selecting themselves
        if(!user.equals(this.clientName)){
            // If the user is on the list, remove them
            // Else, add them to the list
            if (selectedUsers.contains(user))
                selectedUsers.remove(user);
            else
                selectedUsers.add(user);

            setSelectedUsers(selectedUsers);
        }

        // Notify the observer of the changes
        setChanged();
        notifyObservers();
        resetToDefault();
    }

    /**
     * Changes the list of selected users to the whisper group
     */
    public void selectWhisperGroup(){
        setSelectedUsers(new ArrayList<>(this.whisperGroup));

        // Notify the observer of the changes
        setChanged();
        notifyObservers();
        resetToDefault();
    }

    /**
     * Clears the list of selected users
     */
    public void clearSelectedUsers(){
        this.selectedUsers.clear();

        // Notify the observer of the changes
        setChanged();
        notifyObservers();
        resetToDefault();
    }

    /**
     * Starts the name changing process
     */
    public void startNameChange(){
        setChangeName(true);
    }

    /**
     * Checks if the name is available to be changed to
     * If so, the client's name will be changed
     * @param message- Message containing the new client name
     */
    public void finishNameChange(Message message){
        // Get the new username request
        String newUsername= message.getMessage();

        // Check that the username can be used
        // If it can, change the name and send the message to the Server
        if(checkNameChange(newUsername)){
            sendMessage(new Message(clientName, new ArrayList<>(), newUsername));
            setClientName(newUsername);
        }

        // Disallow further name changes until command is reentered
        setChangeName(false);
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

        // If text contains a value
        // Else check for the special server-client interactions
        if(!text.equals("")) {
            // Store the whisper recipients
            if(recipients.size() != users.size()) {
                // Remove yourself from the recipients list
                recipients.remove(clientName);

                // Sets the whisper group to those involved in the whispering
                setWhisperGroup(recipients);
            }

            // If the sender is the Server
            // Cases that don't apply: client joins/leaves chat
            if(message.getSender().equals("Server")){
                // If a user has changed their name
                if(message.getMessage().contains(" has changed their name to: "))
                    // Update selectedUsers and whisperGroup to reflect the change
                    changeUserGroups(message);
            }

            // Display the message
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

            // Tell the GUI that the message couldn't send
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
     * Sets the value of the name the client will be referred to by
     * @param clientName- new username the client will be going by
     */
    private void setClientName(String clientName){
        this.clientName= clientName;
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
     * Gets boolean value of whether or not name is being changed
     */
    public boolean getChangeName(){
        return this.changeName;
    }

    /**
     * Sets boolean to allow/disallow a change of name
     * @param changeName- boolean value determining whether the name is being changed or not
     */
    private void setChangeName(boolean changeName){
        this.changeName= changeName;
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

    /////////////////////////////////////////////////////////////
    //  Client Helper Functions
    /////////////////////////////////////////////////////////////

    /**
     * Checks whether the new username contains appropriate values
     * @param username- the username in question
     * @return boolean of whether or not the name can change
     */
    private boolean checkNameChange(String username){
        // Sets the list of appropriate characters for a username
        String alphabet= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_";

        // If the username is already being used
        if(users.contains(username)){
            // Tell the GUI that the username was already in use
            receiveMessage(new Message("Client", new ArrayList<>(), "Error: Username already in use."));
            return false;
        }

        // Go through each character of the username to make sure it contains appropriate values
        for(int index= 0; index < username.length(); index++){
            // Get the character at the specified index
            String character= String.valueOf(username.charAt(index));

            // If the username is not made of only characters from accepted alphabet
            if(!alphabet.contains(character)){
                // Tell the GUI that the username contains inappropriate values
                receiveMessage(new Message("Client", new ArrayList<>(), "Error: \""+ username+ "\" contains '" + character + "' which is not allowed in user names."));
                return false;
            }
        }

        // Return true if username has passed all the requirements
        return true;
    }

    /**
     * Only called when a user has changed their user name
     * Checks to see if the user is in either whisperGroup or selectedUsers, and if found, changes their name
     * @param message- Message sent from Server about user changing their name
     */
    private void changeUserGroups(Message message){
        // Get the old user name that will be in the lists and the new one to replace the old one
        String[] words= message.getMessage().split(" ");
        String oldUserName= words[0]; String newUserName= words[words.length -1].substring(0, words[words.length -1].length() -1);

        // If selectedUsers contained the old user name
        if(selectedUsers.contains(oldUserName)){
            // Replace the old user name with the new one
            selectedUsers.set(selectedUsers.indexOf(oldUserName), newUserName);
        }

        // If whisperGroup contained the old user name
        if(whisperGroup.contains(oldUserName)){
            // Replace the old user name with the new one
            whisperGroup.set(whisperGroup.indexOf(oldUserName), newUserName);
        }
    }
}
