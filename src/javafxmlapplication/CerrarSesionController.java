package javafxmlapplication;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import model.NavDAOException;
import model.Navigation;
import model.User;
import model.sub.SqliteConnection;

/**
 * Controller for the "Close Session" confirmation dialog.
 * Persists the current session statistics on accept, or returns to the app on cancel.
 */
public class CerrarSesionController implements Initializable {

    @FXML
    private Button bAccept;
    @FXML
    private Button bCancel;

    private int hits;
    private int faults;
    private User user;
    private Navigation nav;

    /**
     * Populates the user context for this dialog.
     *
     * @param user the authenticated user whose session will be persisted
     */
    public void initUser(User user, String nick, String email, String pass,
                         javafx.scene.image.Image avatar, java.time.LocalDate birthday) {
        this.user = user;
    }

    /**
     * Sets the hit/fault counters accumulated during the current work session.
     *
     * @param hits   number of correctly answered problems
     * @param faults number of incorrectly answered problems
     */
    public void initSesion(int hits, int faults) {
        this.hits = hits;
        this.faults = faults;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            SqliteConnection sqlite = new SqliteConnection();
            sqlite.connectSqlite("data.db");
            nav = Navigation.getInstance();
        } catch (NavDAOException ex) {
            Logger.getLogger(CerrarSesionController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(CerrarSesionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleBAcceptOnAction(ActionEvent event) throws Exception {
        user.addSession(hits, faults);
        Platform.exit();
    }

    @FXML
    private void handleBCanceltOnAction(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}
