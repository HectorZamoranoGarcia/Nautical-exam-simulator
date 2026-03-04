package javafxmlapplication;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.YEARS;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.util.converter.LocalDateStringConverter;

import model.NavDAOException;
import model.Navigation;
import model.User;
import model.sub.SqliteConnection;

/**
 * Controller for the user registration window.
 * Validates all fields in real-time and enables the accept button
 * only when all inputs pass their validation rules.
 */
public class FXMLRegistrarseController implements Initializable {

    @FXML
    private Label emailError;
    @FXML
    private Button bAccept;
    @FXML
    private Button bCancel;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label passwordError;
    @FXML
    private DatePicker dateField;
    @FXML
    private Label dateError;
    @FXML
    private TextField usuarioField;
    @FXML
    private Label usuarioError;
    @FXML
    private Button PredAvatarButton;
    @FXML
    private Button subirAvatarButton;

    private BooleanProperty validEmail;
    private BooleanProperty validPassword;
    private BooleanProperty validDate;
    private BooleanProperty validUser;

    private ChangeListener<String> listenerEmail;
    private ChangeListener<String> listenerPassword;
    private ChangeListener<LocalDate> listenerDate;
    private ChangeListener<String> listenerUser;

    private Image avatar;

    /**
     * Checks whether the entered username is unique in the database.
     * Highlights the field and shows an error label if the name already exists.
     */
    private void checkUser() throws NavDAOException {
        String user = usuarioField.getText();
        Navigation nav = Navigation.getInstance();
        boolean isValid = nav.exitsNickName(user);
        validUser.set(isValid);
        showError(!isValid, usuarioField, usuarioError);
    }

    /**
     * Validates the entered email address against the RFC-compatible regex.
     * Highlights the field and shows an error label on failure.
     */
    private void checkEmail() {
        String email = emailField.getText();
        boolean isValid = email.matches(
                "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
        validEmail.set(isValid);
        showError(isValid, emailField, emailError);
    }

    /**
     * Validates the password against the strength rule: 8–15 characters,
     * at least one letter and one digit.
     */
    private void checkPassword() {
        String password = passwordField.getText();
        boolean isValid = password.matches("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,15}$");
        validPassword.set(isValid);
        showError(isValid, passwordField, passwordError);
    }

    /**
     * Validates that the selected birthdate implies the user is at least 16 years
     * old.
     */
    private void checkDate() {
        LocalDate value = dateField.getValue();
        boolean isValid = value.isBefore(LocalDate.now().minus(16, YEARS));
        validDate.set(isValid);
        showError(isValid, dateField, dateError);
    }

    /**
     * Toggles the visibility of an error label and applies a red background
     * to the invalid field.
     *
     * @param isValid      {@code true} if the field contains a valid value
     * @param field        the input control to style
     * @param errorMessage the label to show or hide
     */
    private void showError(boolean isValid, Node field, Node errorMessage) {
        errorMessage.setVisible(!isValid);
        field.setStyle(isValid ? "" : "-fx-background-color: #FCE5E0");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            SqliteConnection sqlite = new SqliteConnection();
            sqlite.connectSqlite("data.db");
        } catch (SQLException ex) {
            // Database file not found; application cannot proceed without it.
        }

        validUser = new SimpleBooleanProperty(false);
        usuarioField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    checkUser();
                    if (validUser.get()) {
                        if (listenerUser != null) {
                            listenerUser = (a, b, c) -> {
                                try {
                                    checkUser();
                                } catch (NavDAOException ignored) {
                                }
                            };
                            usuarioField.textProperty().addListener(listenerUser);
                        }
                    }
                } catch (NavDAOException ignored) {
                }
            }
        });

        validEmail = new SimpleBooleanProperty(false);
        emailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkEmail();
                if (!validEmail.get()) {
                    if (listenerEmail == null) {
                        listenerEmail = (a, b, c) -> checkEmail();
                        emailField.textProperty().addListener(listenerEmail);
                    }
                }
            }
        });

        validPassword = new SimpleBooleanProperty(false);
        passwordField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkPassword();
                if (!validPassword.get()) {
                    if (listenerPassword == null) {
                        listenerPassword = (a, b, c) -> checkPassword();
                        passwordField.textProperty().addListener(listenerPassword);
                    }
                }
            }
        });

        validDate = new SimpleBooleanProperty(false);
        dateField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                checkDate();
                if (!validDate.get()) {
                    if (listenerDate == null) {
                        listenerDate = (a, b, c) -> checkDate();
                        dateField.valueProperty().addListener(listenerDate);
                    }
                }
            }
        });

        LocalDateStringConverter dateConverter = new LocalDateStringConverter() {
            @Override
            public LocalDate fromString(String value) {
                try {
                    return super.fromString(value);
                } catch (Exception e) {
                    return LocalDate.now();
                }
            }
        };
        dateField.setConverter(dateConverter);

        BooleanBinding validFields = Bindings
                .and(validEmail, validPassword)
                .and(validDate)
                .and(validUser.not());

        bAccept.disableProperty().bind(Bindings.not(validFields));
    }

    @FXML
    private void handleBAcceptOnAction(ActionEvent event) throws java.io.IOException, NavDAOException {
        String user = usuarioField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        LocalDate birthdate = dateField.getValue();

        Navigation nav = Navigation.getInstance();
        User registeredUser = nav.registerUser(user, email, password, avatar, birthdate);

        ((javafx.stage.Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void handleBCanceltOnAction(ActionEvent event) {
        ((javafx.stage.Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    @FXML
    private void subirAvatar(ActionEvent event) throws java.io.IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Fichero");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Imágenes", "*.png", "*.jpg"),
                new ExtensionFilter("Todos", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            avatar = new Image(selectedFile.toURI().toString());
        }
    }

    @FXML
    private void predAvatarAction(ActionEvent event) {
        avatar = new Image(getClass().getResourceAsStream("/resources/usuario.png"));
    }
}
