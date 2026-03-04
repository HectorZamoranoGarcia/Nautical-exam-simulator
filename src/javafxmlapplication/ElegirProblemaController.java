package javafxmlapplication;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import model.NavDAOException;
import model.Navigation;
import model.Problem;
import model.User;
import model.sub.SqliteConnection;

/**
 * Controller for the problem selection screen.
 * Allows the user to pick a problem from the list by double-clicking,
 * or to choose one at random via the random-mode button.
 */
public class ElegirProblemaController implements Initializable {

    @FXML
    private Label labelProblemas;
    @FXML
    private Button botonModoAleatorio;
    @FXML
    private ListView<String> listaProblemas;

    private String nick;
    private String pass;

    private List<Problem> problemList;
    private ObservableList<Problem> observableProblems;
    private Navigation nav;
    private User user;

    private Problem selectedProblem;
    private int selectionIndex;
    private boolean problemSelected;

    /**
     * Populates user context needed to re-authenticate and load the problem list.
     *
     * @param user     the authenticated user object
     * @param nick     the user's nickname (used for re-authentication)
     * @param email    the user's email address
     * @param pass     the user's password (used for re-authentication)
     * @param avatar   the user's avatar image
     * @param birthday the user's date of birth
     */
    public void initUser(User user, String nick, String email, String pass,
            javafx.scene.image.Image avatar, java.time.LocalDate birthday) {
        this.user = user;
        this.nick = nick;
        this.pass = pass;
    }

    /**
     * Provides the accumulated hit/fault counters from the current session.
     *
     * @param hits   problems answered correctly so far
     * @param faults problems answered incorrectly so far
     */
    public void initSession(int hits, int faults) {
        // Counters are carried over to the parent controller on return.
    }

    /**
     * Returns whether the user has double-clicked to confirm a problem selection.
     *
     * @return {@code true} if a problem was selected, {@code false} otherwise
     */
    public boolean isProblemSelected() {
        return problemSelected;
    }

    /**
     * Returns the problem chosen by the user, or {@code null} if none was selected.
     *
     * @return the selected {@link Problem}
     */
    public Problem getProblem() {
        return selectedProblem;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            nav = Navigation.getInstance();
            user = nav.authenticate(nick, pass);
            SqliteConnection sqlite = new SqliteConnection();
            sqlite.connectSqlite("data.db");
        } catch (NavDAOException ex) {
            Logger.getLogger(ElegirProblemaController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ElegirProblemaController.class.getName()).log(Level.SEVERE, null, ex);
        }

        problemSelected = false;
        problemList = nav.getProblems();
        observableProblems = FXCollections.observableList(problemList);

        listaProblemas.getItems().addAll(
                "Problema 1", "Problema 2", "Problema 3", "Problema 4", "Problema 5", "Problema 6",
                "Problema 7", "Problema 8", "Problema 9", "Problema 10", "Problema 11", "Problema 12",
                "Problema 13", "Problema 14", "Problema 15", "Problema 16", "Problema 17", "Problema 18");

        listaProblemas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                selectionIndex = listaProblemas.getSelectionModel().getSelectedIndex();
                selectedProblem = observableProblems.get(selectionIndex);
                if (selectedProblem != null) {
                    problemSelected = true;
                    listaProblemas.getScene().getWindow().hide();
                }
            }
        });
    }

    /** Selects a random problem from the available list and closes the window. */
    @FXML
    private void botonModoAleatorioOnAction(ActionEvent event) {
        if (problemList != null && !problemList.isEmpty()) {
            int randomIndex = (int) (Math.random() * problemList.size());
            listaProblemas.getSelectionModel().select(randomIndex);
            selectedProblem = observableProblems.get(randomIndex);

            if (selectedProblem != null) {
                problemSelected = true;
                listaProblemas.scrollTo(randomIndex);
                listaProblemas.getScene().getWindow().hide();
            }
        }
    }
}
