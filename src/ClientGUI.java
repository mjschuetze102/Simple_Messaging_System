import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates the GUI for the clients to view
 * Created by Michael on 12/10/2016.
 * TODO: Finish the sendMessage and receiveMessage functions
 */

public class ClientGUI extends Application {

    ///////////////////////////
    // Predetermined Variables
    ///////////////////////////

    /** Name of the host IPAddress */
    final String HOST = "localhost";

    /** The underlying client model */
    private Client client;

    /** The name of the client */
    private String clientName;

    ///////////////////////////
    // GUI Field Variables
    ///////////////////////////

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
     * Construct a Client object
     */
    public ClientGUI() {
        this.client= new Client(HOST);
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
        stage.setOnCloseRequest(event -> {onClose();});

        ArrayList<String> testing= new ArrayList<>();
        testing.add("Oscar"); testing.add("Juri"); testing.add("Michael"); // Comment this out to test other display
        Message message= new Message("Client", testing, "Hello");
        receiveMessage(message);
    }

    /**
     * Called when program closes to tell server user has left
     */
    private void onClose(){
        // Get the values of each of the fields
        String clientName= this.clientName;
        ArrayList<String> recipients= getRecipients();

        // Create a new special case message for server to interpret
        Message message= new Message(clientName, recipients, "");

        // Call the sendMessage function with the message to send
        sendMessage(message);
    }

    /////////////////////////////////////////////////////////////
    //  GUI Helper Functions
    /////////////////////////////////////////////////////////////

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
        deselect.setOnAction(event -> {userList.getSelectionModel().clearSelection();});
        textInput.setOnKeyReleased(event -> {toggleSend();});
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

    /////////////////////////////////////////////////////////////
    //  Message Functions
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

        // If text contains a value, display the message
        if(!text.equals(""))
            displayMessage(text, sender, recipients);
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
    private void displayMessage(String text, String sender, ArrayList<String> recipients){
        // Create the start of the string that will be displayed
        String message= "\n["+ sender+ "] ";

        // If the sender selected people to whisper to
        if(recipients.size() != userList.getItems().size() && recipients.size() <= 1){
            message+= "{ ";
            // Loop through each recipient and add their name to the list
            for(int index= 0; index < recipients.size() -1; index++){
                message+= recipients.get(index)+ "/ ";
            }
            // Add the last recipient to the string to avoid an extra '/'
            message+= recipients.get(recipients.size() -1)+ " }";
        }

        // Message will either be in '[client]-' or '[client] { recipient }-' form
        message+= "- "+ text;

        // Add the message to the end of the TextArea
        messageArea.appendText(message);
    }

    /**
     * Collects the values from the GUI fields
     * Creates a new message object
     * Calls the sendMessage function with the message being sent
     */
    private void createMessage(){
        // Get the values of each of the fields
        String text= this.textInput.getText();
        String clientName= this.clientName;
        ArrayList<String> recipients= getRecipients();

        // Reset the textInput field to blank and the send button to disabled
        this.textInput.setText("");
        toggleSend();

        // Create a new instance of the Message class
        Message message= new Message(clientName, recipients, text);

        // Call the sendMessage function with the message to send
        sendMessage(message);
    }

    /**
     * Sends a message to the server for interpretation
     * @param message- Message object containing sender, recipients, and text
     */
    private void sendMessage(Message message){
        //
    }

    /////////////////////////////////////////////////////////////
    //  Message Helper Functions
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
        if(!userList.getSelectionModel().getSelectedItems().isEmpty())
            recipients= new ArrayList<>(userList.getSelectionModel().getSelectedItems());
        else
            recipients= new ArrayList<>(this.userList.getItems());

        // Return the new ArrayList containing all the recipients
        return recipients;
    }

    /**
     * Simply runs the application
     * @param args- program arguments, none are used in this case
     */
    public static void main(String[] args){
        Application.launch(args);
    }
}
