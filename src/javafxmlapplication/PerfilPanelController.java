package javafxmlapplication;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import model.NavDAOException;
import model.Navigation;
import model.User;
import model.sub.SqliteConnection;

/**
 * Controller for the user profile edit dialog.
 * Allows the user to update their password, email, avatar, and birthdate.
 */
public class PerfilPanelController implements Initializable {

    @FXML
    private TextField tfUsuario;
    @FXML
    private PasswordField pfContrasena;
    @FXML
    private TextField tfEmail;
    @FXML
    private DatePicker dpFechaNacimiento;
    @FXML
    private ImageView ivDialogIcon;

    private String nick;
    private String pass;
    private boolean saveConfirmed;
    private User user;
    private Navigation nav;

    /**
     * Stores the user credentials needed to re-authenticate before saving changes.
     *
     * @param nick     the user's current nickname
     * @param email    the user's current email address
     * @param pass     the user's current password
     * @param avatar   the user's current avatar image
     * @param birthday the user's date of birth
     */
    public void initUser(String nick, String email, String pass, Image avatar, LocalDate birthday) {
        this.nick = nick;
        this.pass = pass;
    }

    /**
     * Pre-fills the dialog fields with the current user data.
     *
     * @param nick     the user's current nickname
     * @param email    the user's current email address
     * @param pass     the user's current password
     * @param avatar   the user's current avatar image
     * @param birthday the user's date of birth
     */
    public void populateFields(String nick, String email, String pass, Image avatar, LocalDate birthday) {
        tfUsuario.setText(nick);
        pfContrasena.setText(pass);
        tfEmail.setText(email);
        ivDialogIcon.setImage(avatar);
        dpFechaNacimiento.setValue(birthday);
    }

    /**
     * Returns whether the user confirmed saving the profile changes.
     *
     * @return {@code true} if the save button was pressed successfully
     */
    public boolean isSaveConfirmed() {
        return saveConfirmed;
    }

    /**
     * Returns the updated user object after a successful save.
     *
     * @return the updated {@link User}
     */
    public User getUser() {
        return user;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        saveConfirmed = false;

        try {
            SqliteConnection sqlite = new SqliteConnection();
            sqlite.connectSqlite("data.db");
        } catch (SQLException ex) {
            Logger.getLogger(PerfilPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            nav = Navigation.getInstance();
            user = nav.authenticate(nick, pass);
        } catch (NavDAOException ex) {
            Logger.getLogger(PerfilPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Opens a file chooser so the user can pick a new avatar image. */
    @FXML
    void onChooseAvatar() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecciona un avatar");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        Window win = ivDialogIcon.getScene().getWindow();
        java.io.File file = chooser.showOpenDialog(win);
        if (file != null) {
            ivDialogIcon.setImage(new Image(file.toURI().toString()));
        }
    }

    /** Validates and persists all profile changes, then closes the dialog. */
    @FXML
    void onGuardar() throws NavDAOException {
        user = nav.authenticate(nick, pass);
        if (pfContrasena.getText().length() < 8) {
            showError("La contraseña debe tener al menos 8 caracteres.");
            return;
        } else {
            user.setPassword(pfContrasena.getText());
            user.setEmail(tfEmail.getText());
            user.setAvatar(ivDialogIcon.getImage());
            user.setBirthdate(dpFechaNacimiento.getValue());
            saveConfirmed = true;
        }
        ivDialogIcon.getScene().getWindow().hide();
        showInfo("Cambios guardados, para su visualización debe reiniciar el programa.");
    }

    /** Discards any changes and closes the dialog. */
    @FXML
    void onCancelar() {
        ivDialogIcon.getScene().getWindow().hide();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.initOwner(ivDialogIcon.getScene().getWindow());
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.initOwner(ivDialogIcon.getScene().getWindow());
        alert.showAndWait();
    }
}
