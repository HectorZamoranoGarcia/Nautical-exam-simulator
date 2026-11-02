package javafxmlapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/** Ruler rotation controller */
public class RotarReglaController {

    @FXML
    private Slider rotationSlider;
    @FXML
    private Label rotationLabel;
    @FXML
    private Text titleText;

    private double xOffset = 0;
    private double yOffset = 0;

    private ImageView ruleImageView;

    public void setRuleImageView(ImageView ruleImageView) {
        this.ruleImageView = ruleImageView;
    }

    @FXML
    public void initialize() {

        // Drag the window by its title bar

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
            rotationLabel.setText(String.format("%.2f°", rotationDegrees));
            ruleImageView.setRotate(rotationDegrees);
        });
    }

    @FXML
    private void closeRotationWindow(ActionEvent event) {
        Stage stage = (Stage) rotationSlider.getScene().getWindow();
        stage.close();
    }
}
