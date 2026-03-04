package javafxmlapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import model.NavDAOException;
import model.Navigation;
import model.User;
import model.sub.SqliteConnection;

/**
 * Controller for the login (authentication) window.
 * Validates credentials and opens the main work screen on success.
 */
public class FXMLAutenticarseController implements Initializable {

    @FXML
    private Label emailError;
    @FXML
    private Label passwordError;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField usuarioField;
    @FXML
    private javafx.scene.control.Button bAccept;
    @FXML
    private javafx.scene.control.Button bCancel;
    @FXML
    private javafx.scene.control.Button RegistrarseButton;

    private int hits = 0;
    private int faults = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            SqliteConnection sqlite = new SqliteConnection();
            sqlite.connectSqlite("data.db");
            try {
                Navigation.getInstance();
            } catch (NavDAOException ex) {
                // Navigation singleton not yet ready; will be retried on login action.
            }
        } catch (SQLException ex) {
            // Database file not found; application cannot proceed without it.
        }
    }

    /**
     * Authenticates the user with the entered credentials and opens the main screen.
     * Shows nothing if the nickname does not exist or credentials are incorrect.
     */
    @FXML
    private void handleBAcceptOnAction(ActionEvent event) throws IOException, NavDAOException {
        String nick = usuarioField.getText();
        String pass = passwordField.getText();
        Navigation nav = Navigation.getInstance();

        if (nav.exitsNickName(nick)) {
            User user = nav.authenticate(nick, pass);
            if (user.chekCredentials(nick, pass)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/FXMLTrabajo.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo1.png")));
                stage.setScene(scene);
                stage.setTitle("Trabajo");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setMinWidth(900);
                stage.setMinHeight(630);

                FXMLTrabajoController workController = loader.getController();
                workController.initUser(user, user.getNickName(), user.getEmail(),
                        user.getPassword(), user.getAvatar(), user.getBirthdate());
                workController.initSes(hits, faults);

                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                stage.show();
            }
        }
    }

    @FXML
    private void handleBCanceltOnAction(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).hide();
    }

    @FXML
    private void RegistrarseButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VentanaRegistro.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 500);
        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo1.png")));
        stage.setScene(scene);
        stage.setTitle("Registrarse");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }
}
