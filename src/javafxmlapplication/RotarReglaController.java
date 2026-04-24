package javafxmlapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Controller for the "Rotate Ruler" tool window.
 * Exposes a slider that rotates the ruler overlay on the navigation chart.
 * The title bar is draggable since this window has no OS decoration.
 */
public class RotarReglaController {

    @FXML
    private Slider rotationSlider;
    @FXML
    private Label rotationLabel;
    @FXML
    private Text titleText;

    /**
     * X/Y offsets used to compute window position while the title bar is dragged.
     */
    private double xOffset = 0;
    private double yOffset = 0;

    private ImageView ruleImageView;

    /**
     * Injects the ruler {@link ImageView} from the main chart pane so that
     * slider changes are applied directly to the correct overlay node.
     *
     * @param ruleImageView the ImageView representing the ruler on the chart
     */
    public void setRuleImageView(ImageView ruleImageView) {
        this.ruleImageView = ruleImageView;
    }

    @FXML
    public void initialize() {
        // Allow dragging the undecorated window by its title bar.
        titleText.setOnMousePressed(event -> {
            Stage stage = (Stage) titleText.getScene().getWindow();
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });

        titleText.setOnMouseDragged(event -> {
            Stage stage = (Stage) titleText.getScene().getWindow();
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });

        rotationSlider.setMin(0);
        rotationSlider.setMax(360);
        rotationSlider.setValue(0);

        rotationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double rotationDegrees = newValue.doubleValue();
            rotationLabel.setText(String.format("%.2fÃ‚Â°", rotationDegrees));
            ruleImageView.setRotate(rotationDegrees);
        });
    }

    @FXML
    private void closeRotationWindow(ActionEvent event) {
        Stage stage = (Stage) rotationSlider.getScene().getWindow();
        stage.close();
    }
}
