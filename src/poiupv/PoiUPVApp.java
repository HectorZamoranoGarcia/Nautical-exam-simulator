package poiupv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/** JavaFX application entry point */
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

    public static void reiniciarApp() throws Exception {
        Stage newStage = new Stage();
        instance.start(newStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
