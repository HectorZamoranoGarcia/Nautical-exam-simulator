package javafxmlapplication;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the "Close Application" confirmation dialog.
 * Exits the JVM on accept, or dismisses the dialog on cancel.
 */
public class CerrarAplicacionController implements Initializable {

    @FXML
    private Button bAccept;
    @FXML
    private Button bCancel;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Nothing to initialize.
    }

    @FXML
    private void handleBAcceptOnAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void handleBCanceltOnAction(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).hide();
    }
}
