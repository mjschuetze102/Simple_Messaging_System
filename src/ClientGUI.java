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
    private List<String> users;

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
        users.setPrefHeight(350);
        users.setPrefWidth(200);
        users.setPadding(new Insets(10));
        users.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        right.getChildren().add(users);
        VBox.setMargin(users, new Insets(10, 10, 0, 10));

        // Button in the bottom right
        // When clicked will deselect all of the names from the ListView
        Button deselect= new Button("Deselect");
        deselect.setOnAction(event -> {} );
        deselect.setPrefWidth(200);
        right.getChildren().add(deselect);
        VBox.setMargin(deselect, new Insets(0, 10, 10, 10));

        // Add VBox to the BorderPane
        border.setRight(right);



        /**
         * Left of the BorderPane
         */
        Pane left= new VBox();

        // TextArea in the top left
        // Displays messages
        TextArea messageArea = new TextArea();
        messageArea.setPrefHeight(340);
        messageArea.setPrefWidth(475);
        left.getChildren().add(messageArea);
        VBox.setMargin(messageArea, new Insets(10, 0, 10, 10));

        /** HBox to line up TextField and Button */
        Pane bottom= new HBox();
        VBox.setMargin(bottom, new Insets(0, 0, 10, 10));

        // TextField in the bottom left
        // Displays messages
        TextField textInput= new TextField();
        textInput.setPrefWidth(375);
        bottom.getChildren().add(textInput);
        HBox.setMargin(textInput, new Insets(0, 10, 0, 0));


        // Button in the bottom left
        // When clicked will send messages to the system
        Button send= new Button("Send");
        send.setOnAction(event -> {} );
        send.setPrefWidth(80);
        bottom.getChildren().add(send);
        HBox.setMargin(send, new Insets(0, 0, 0, 10));

        /** Add HBox to the VBox */
        left.getChildren().add(bottom);

        // Add the VBox to the BorderPane
        border.setLeft(left);



        /** Stage Properties */
        stage.setTitle("Simple Messaging System");
        stage.setScene(new Scene(border));
        stage.show();
    }

    public static void main(String[] args){
        Application.launch(args);
    }
}
