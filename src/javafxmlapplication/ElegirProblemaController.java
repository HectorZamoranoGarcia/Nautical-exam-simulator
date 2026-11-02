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

/** Problem selection controller */
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

    public void initUser(User user, String nick, String email, String pass,
            javafx.scene.image.Image avatar, java.time.LocalDate birthday) {
        this.user = user;
        this.nick = nick;
        this.pass = pass;
    }

    public void initSession(int hits, int faults) {

        // Keep session totals when returning

    }

    public boolean isProblemSelected() {
        return problemSelected;
    }

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
