package ClientSide;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Creates the GUI for the clients to view
 * Created by Michael on 12/10/2016.
 */

public class ClientGUI extends Application implements Observer {

    ///////////////////////////
    // ClientSide.ClientGUI Variables
    ///////////////////////////

    /** The underlying client model */
    private Client client;

    /** A List representing the list of users */
    private ListView<String> userList;

    /** A TextArea where users will see messages displayed */
    private TextArea messageArea;

    /** A TextField where users type in a message to send */
    private TextField textInput;

    /** A Button that allows users to send messages */
    private Button send;

    /////////////////////////////////////////////////////////////
    //  GUI Functions
    /////////////////////////////////////////////////////////////

    /**
     * Construct a ClientSide.Client object
     */
    public ClientGUI() {
        this.client= new Client();
        this.client.addObserver(this);
    }

    /**
     * Starts up the GUI
     * @param stage the background for the GUI
     */
    public void start(Stage stage){
        // The main pane for the stage
        BorderPane border= new BorderPane();



        /**
         * Right side of the BorderPane
         */
        Pane right= new VBox();

        // ListView in the top right
        // Displays and selects the users to send messages to
        ObservableList<String> names = FXCollections.observableArrayList(
                "Julia", "Ian", "Sue", "Matthew", "Hannah", "Stephan", "Denise");
        ListView<String> userList = new ListView<>(names);
        // Set userList.getItems() to this.userList and add it to the right VBox
        this.userList= userList;
        right.getChildren().add(userList);

        // Button in the bottom right
        // When clicked will deselect all of the names from the ListView
        Button deselect= new Button("Deselect");
        // Add deselect to the right VBox
        right.getChildren().add(deselect);

        // Add VBox to the BorderPane
        border.setRight(right);



        /**
         * Left of the BorderPane
         */
        Pane left= new VBox();

        // TextArea in the top left
        // Displays messages
        TextArea messageArea = new TextArea();
        // Set messageArea to this.messageArea and add it to the left VBox
        this.messageArea= messageArea;
        left.getChildren().add(messageArea);

        /** HBox to line up TextField and Button */
        Pane bottom= new HBox();

        // TextField in the bottom left
        // Displays messages
        TextField textInput= new TextField();
        // Set textInput to this.textInput and add it to the bottom HBox
        this.textInput= textInput;
        bottom.getChildren().add(textInput);

        // Button in the bottom left
        // When clicked will send messages to the system
        Button send= new Button("Send");
        // Set send to this.send and add it to the bottom HBox
        this.send= send;
        bottom.getChildren().add(send);

        // Add HBox to the left VBox
        left.getChildren().add(bottom);

        // Add the VBox to the BorderPane
        border.setLeft(left);



        /** Calls to the functions that change the fields from default values */
        setDimensions(userList, deselect, messageArea, textInput, send);
        setMargins(userList, deselect, messageArea, bottom, textInput, send);
        defineProperties(userList, deselect, messageArea, textInput, send);

        // Set the send button to disabled at the start
        toggleSend();

        /** Stage Properties */
        stage.setTitle("Simple Messaging System");
        stage.setScene(new Scene(border));
        stage.setResizable(false);
        stage.show();

        // Function called when programmed is closed
        stage.setOnCloseRequest(event -> {this.client.onClose();});

        // Start ClientSide.Client
        this.client.startRunning();
    }

    /**
     * Handles all of the heights and widths of the various fields on our BorderPane
     * @param userList- ListView
     * @param deselect- Button
     * @param messageArea- TextArea
     * @param textInput- TextField
     * @param send- Button
     */
    private void setDimensions(ListView<String> userList, Button deselect, TextArea messageArea,
                               TextField textInput, Button send){
        userList.setPrefHeight(350);
        userList.setPrefWidth(200);

        deselect.setPrefWidth(200);

        messageArea.setPrefHeight(340);
        messageArea.setPrefWidth(475);

        textInput.setPrefWidth(375);

        send.setPrefWidth(80);
    }

    /**
     * Handles all of the margins of the various fields on our BorderPane
     * @param userList- ListView
     * @param deselect- Button
     * @param messageArea- TextArea
     * @param bottom- HBox
     * @param textInput- TextField
     * @param send- Button
     */
    private void setMargins(ListView<String> userList, Button deselect, TextArea messageArea,
                            Pane bottom, TextField textInput, Button send){
        // Sets the margin for the right side of screen
        VBox.setMargin(userList, new Insets(10, 10, 0, 10));
        VBox.setMargin(deselect, new Insets(0, 10, 10, 10));

        // Sets the margin for the left side of screen
        VBox.setMargin(messageArea, new Insets(10, 0, 10, 10));
        VBox.setMargin(bottom, new Insets(0, 0, 10, 10));
        HBox.setMargin(textInput, new Insets(0, 10, 0, 0));
        HBox.setMargin(send, new Insets(0, 0, 0, 10));

        // Set Padding for userList
        userList.setPadding(new Insets(10));
    }

    /**
     * Handles adding the properties to the fields
     * @param userList- ListView
     * @param deselect- Button
     * @param messageArea- TextArea
     * @param textInput- TextField
     * @param send- Button
     */
    private void defineProperties(ListView<String> userList, Button deselect, TextArea messageArea,
                                  TextField textInput, Button send){
        // Event Handlers for fields
        userList.setOnMouseClicked(event -> {this.client.changeSelectedUsers(userList.getSelectionModel().getSelectedItem());});
        deselect.setOnAction(event -> {userList.getSelectionModel().clearSelection();});
        textInput.setOnKeyReleased(event -> {toggleSend(); actionCommands();});
        send.setOnAction(event -> {createMessage();});

        // Allow for multiple users to be selected from the ListView
        userList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        // Allow the Enter key to activate button
        send.setDefaultButton(true);

        // Set the TextArea to uneditable and to wrap text
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
    }

    /**
     * Enables/Disables the send button based on whether or not textInput is empty
     */
    private void toggleSend(){
        // If the textInput field is empty, disable the send button
        // Else when there is text in the textInput field, enable the send button
        if(this.textInput.getText().equals(""))
            this.send.setDisable(true);
        else
            this.send.setDisable(false);
    }

    /**
     * Append the message to the bottom of the TextArea
     * Displayed in either:
     *      [client]- Message
     *      [client] { recipient/recipient }- Message
     * @param text- message being displayed to the user
     * @param sender- the user who sent the message
     * @param recipients- the users the message was sent to
     */
    private void setDisplay(String text, String sender, ArrayList<String> recipients){
        // Create the start of the string that will be displayed
        String message= "\n["+ sender+ "] ";

        // If the sender selected people to whisper to
        if(recipients.size() != userList.getItems().size() && recipients.size() > 1){
            message+= "{ ";
            // Loop through each recipient and add their name to the list
            for(int index= 0; index < recipients.size() -1; index++){
                message+= recipients.get(index)+ "/ ";
            }
            // Add the last recipient to the string to avoid an extra '/'
            message+= recipients.get(recipients.size() -1)+ " } ";
        }

        // Message will either be in '[client]-' or '[client] { recipient }-' form
        message+= "- "+ text;

        // Add the message to the end of the TextArea
        messageArea.appendText(message);
    }

    /////////////////////////////////////////////////////////////
    //  GUI Interactions with Model
    /////////////////////////////////////////////////////////////

    /**
     * Sets the ListView to contain the new recipients list
     * @param recipients- the users the message was sent to
     */
    private void setUserList(ArrayList<String> recipients){
        // Create an observable list from the list of recipients
        ObservableList<String> names= FXCollections.observableArrayList(recipients);
        userList.setItems(names);
    }

    /**
     * Selects the people who were included in the recipient list
     * @param recipients- the users the message was sent to
     */
    private void setSelectedUserList(ArrayList<String> recipients){
        // Clear the currently selected list
        this.userList.getSelectionModel().clearSelection();

        // For each recipient in recipients find and add them to the selected users
        for(String recipient: recipients)
            this.userList.getSelectionModel().select(recipient);
    }

    /**
     * Gets the message from the model and interprets whether or not to display it
     * @param message- the message received from the model
     */
    private void receiveMessage(Message message){
        // If there wasn't a message change don't display anything
        if(message == null)
            return;

        // Send the message to be displayed in TextArea
        setDisplay(message.getMessage(), message.getSender(), message.getReceivers());
    }

    /**
     * Gets the error from the model and interprets whether or not to display it
     * @param error- the error being received from the model
     */
    private void checkError(String error){
        // If there wasn't a error change don't display anything
        if(error == null)
            return;

        // Send the error to be displayed in TextArea
        setDisplay(error, "Server", new ArrayList<>());
    }

    /////////////////////////////////////////////////////////////
    //  GUI Helper Functions
    /////////////////////////////////////////////////////////////

    /**
     * Get the selected recipients, or get all if none are selected
     * @return ArrayList containing all the recipients
     */
    private ArrayList<String> getRecipients(){
        // Create a new recipients variable that will store a list of recipients for a message
        ArrayList<String> recipients;

        // If there are selected items, copy them into an ArrayList
        // Else create a new ArrayList with all items
        if(!this.client.getSelectedUsers().isEmpty())
            recipients= new ArrayList<>(this.client.getSelectedUsers());
        else
            recipients= new ArrayList<>(this.client.getUsers());

        // Return the new ArrayList containing all the recipients
        return recipients;
    }

    /**
     * Collects the values from the GUI fields
     * Creates a new message object
     * Calls the sendMessage function with the message being sent
     */
    private void createMessage(){
        // Get the values of each of the fields
        String text= this.textInput.getText();
        String clientName= this.client.getClientName();
        ArrayList<String> recipients= getRecipients();

        // Reset the textInput field to blank and the send button to disabled
        this.textInput.setText("");
        toggleSend();

        // Create a new instance of the Message class
        Message message= new Message(clientName, recipients, text);

        // Call the sendMessage function with the message to send
        this.client.sendMessage(message);
    }

    /**
     * Checks the first two characters of TextField
     * If the first character is a '-' or '/'
     *      If the second character is a 'r' or 'w'
     *          Respond to the group of people tat were part of the last whisper
     */
    private void actionCommands(){
        // Get the text from the textInput
        String text= this.textInput.getText();

        // If there are not two characters in TextField, return as this no longer applies
        if(text.length() != 2)
            return;

        // Check the first character to make sure it's an action character
        if(text.charAt(0) == '-' || text.charAt(0) == '/'){
            // Check if the second character sets off the respond command
            if(text.charAt(1) == 'r' || text.charAt(1) == 'w'){
                // Check that the conditions for replying to a whisper are met
                if(this.client.getWhisperGroup() == null)
                    return;

                // Clear TextField, toggle Send, and change ListView
                this.textInput.clear();
                toggleSend();
                setSelectedUserList(this.client.getWhisperGroup());
            }
        }
    }

    /**
     * Update the UI when the model calls update.
     * The update may change the appearance of the TextArea, and ListView.
     * The update makes calls to the public interface of the model components to determine the new values to display.
     * @param t An Observable -- not used.
     * @param o An Object -- not used.
     */
    public void update(Observable t, Object o){
        ArrayList<String> recipients= this.client.getUsers();
        ArrayList<String> selectedUsers= this.client.getSelectedUsers();
        Message message= this.client.getMessage();
        String error= this.client.getError();

        setUserList(recipients);
        setSelectedUserList(selectedUsers);
        receiveMessage(message);
        checkError(error);
    }

    /**
     * Simply runs the application
     * @param args- program arguments, none are used in this case
     */
    public static void main(String[] args){
        Application.launch(args);
    }
}
