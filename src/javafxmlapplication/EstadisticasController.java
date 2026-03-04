package javafxmlapplication;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;

import model.NavDAOException;
import model.Navigation;
import model.User;
import model.sub.SqliteConnection;

import java.time.LocalDate;

/**
 * Controller for the statistics overview screen.
 * Displays aggregate hit/fault totals and their percentages.
 */
public class EstadisticasController implements Initializable {

    @FXML
    private ToggleGroup Grupotiempo;
    @FXML
    private ToggleGroup Grupodificultad;
    @FXML
    private ToggleGroup Grupotematica;

    @FXML
    private Label labelProblemas;
    @FXML
    private Label labelPorAciertos;
    @FXML
    private Label labelNumAciertos;
    @FXML
    private Label labelPorFallos;
    @FXML
    private Label labelNumFallos;

    private String nick;
    private String pass;

    /**
     * Populates the user context required for the statistics query.
     *
     * @param nick     the user's nickname
     * @param email    the user's email address
     * @param pass     the user's password
     * @param avatar   the user's avatar image
     * @param birthday the user's date of birth
     */
    public void initUser(String nick, String email, String pass, Image avatar, LocalDate birthday) {
        this.nick = nick;
        this.pass = pass;
    }

    /**
     * Calculates and displays aggregate statistics from the raw hit/fault counts.
     *
     * @param totalHits   total number of correctly answered questions across all sessions
     * @param totalFaults total number of incorrectly answered questions across all sessions
     */
    public void initEstadisticas(int totalHits, int totalFaults) {
        double numProblemas = totalHits + totalFaults;
        double porcentajeAciertos = ((totalHits / numProblemas) * 100);
        double porcentajeFallos = ((totalFaults / numProblemas) * 100);

        labelProblemas.setText(numProblemas + "");
        labelPorAciertos.setText(porcentajeAciertos + "%");
        labelNumAciertos.setText(totalHits + "");
        labelPorFallos.setText(porcentajeFallos + "%");
        labelNumFallos.setText(totalFaults + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            SqliteConnection sqlite = new SqliteConnection();
            sqlite.connectSqlite("data.db");
        } catch (SQLException ex) {
            Logger.getLogger(EstadisticasController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            Navigation nav = Navigation.getInstance();
            User authenticatedUser = nav.authenticate(nick, pass);
        } catch (NavDAOException ex) {
            Logger.getLogger(EstadisticasController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
