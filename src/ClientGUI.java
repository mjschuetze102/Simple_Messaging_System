import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

/**
 * Creates the GUI for the clients to view
 * Created by Michael on 12/10/2016.
 */

public class ClientGUI extends Application {

    // Predetermined Variables
    final String HOST = "localhost";

    /** The underlying client model */
    private Client client;

    /** A List representing the list of users */
    //private List<String> users;

    /** The name of the client */
    private String clientName;

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
        ListView<String> users = new ListView<>(names);
        users.setPadding(new Insets(10));
        users.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        right.getChildren().add(users);

        // Button in the bottom right
        // When clicked will deselect all of the names from the ListView
        Button deselect= new Button("Deselect");
        deselect.setOnAction(event -> {} );
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
        left.getChildren().add(messageArea);

        /** HBox to line up TextField and Button */
        Pane bottom= new HBox();

        // TextField in the bottom left
        // Displays messages
        TextField textInput= new TextField();
        bottom.getChildren().add(textInput);

        // Button in the bottom left
        // When clicked will send messages to the system
        Button send= new Button("Send");
        send.setOnAction(event -> {} );
        bottom.getChildren().add(send);

        /** Add HBox to the VBox */
        left.getChildren().add(bottom);

        // Add the VBox to the BorderPane
        border.setLeft(left);



        /** Calls to the functions that change the fields from default values */
        setDimensions(users, deselect, messageArea, textInput, send);
        setMargins(users, deselect, messageArea, bottom, textInput, send);

        /** Stage Properties */
        stage.setTitle("Simple Messaging System");
        stage.setScene(new Scene(border));
        stage.show();
    }

    /**
     * Handles all of the heights and widths of the various fields on our BorderPane
     * @param users- ListView
     * @param deselect- Button
     * @param messageArea- TextArea
     * @param textInput- TextField
     * @param send- Button
     */
    private void setDimensions(ListView<String> users, Button deselect, TextArea messageArea,
                                  TextField textInput, Button send){
        users.setPrefHeight(350);
        users.setPrefWidth(200);

        deselect.setPrefWidth(200);

        messageArea.setPrefHeight(340);
        messageArea.setPrefWidth(475);

        textInput.setPrefWidth(375);

        send.setPrefWidth(80);
    }

    /**
     * Handles all of the margins of the various fields on our BorderPane
     * @param users- ListView
     * @param deselect- Button
     * @param messageArea- TextArea
     * @param bottom- HBox
     * @param textInput- TextField
     * @param send- Button
     */
    private void setMargins(ListView<String> users, Button deselect, TextArea messageArea,
                            Pane bottom, TextField textInput, Button send){
        // Sets the margin for the right side of screen
        VBox.setMargin(users, new Insets(10, 10, 0, 10));
        VBox.setMargin(deselect, new Insets(0, 10, 10, 10));

        // Sets the margin for the left side of screen
        VBox.setMargin(messageArea, new Insets(10, 0, 10, 10));
        VBox.setMargin(bottom, new Insets(0, 0, 10, 10));
        HBox.setMargin(textInput, new Insets(0, 10, 0, 0));
        HBox.setMargin(send, new Insets(0, 0, 0, 10));
    }

    public static void main(String[] args){
        Application.launch(args);
    }
}
