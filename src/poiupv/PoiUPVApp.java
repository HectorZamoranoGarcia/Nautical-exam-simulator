package poiupv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX application entry point for the Nautical Exam Simulator.
 * Loads the authentication window on first launch and exposes a static
 * method to re-open that window without restarting the JVM.
 */
public class PoiUPVApp extends Application {

    private static Stage primaryStage;
    private static PoiUPVApp instance;

    @Override
    public void start(Stage stage) throws Exception {
        instance = this;
        primaryStage = stage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VentanaAutenticacion.fxml"));
        Parent root = loader.load();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo1.png")));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Bienvenido");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(660);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }

    /**
     * Creates a new top-level window and re-launches the authentication screen.
     * Used to return to login after a session ends without restarting the JVM.
     *
     * @throws Exception if the FXML file cannot be loaded
     */
    public static void reiniciarApp() throws Exception {
        Stage newStage = new Stage();
        instance.start(newStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
