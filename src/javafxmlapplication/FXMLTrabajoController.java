package javafxmlapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import model.Answer;
import model.NavDAOException;
import model.Navigation;
import model.Problem;
import model.User;
import model.sub.SqliteConnection;
import poiupv.Poi;

/**
 * Primary controller for the nautical exam workspace (FXMLTrabajo.fxml).
 *
 * <p>
 * This controller manages the full working environment: the zoomable/pannable
 * chart pane, all drawing tools (point, line, arc, text, protractor, ruler),
 * the problem lifecycle (select → answer → score), and user session management.
 * </p>
 */
public class FXMLTrabajoController implements Initializable {

    // ── Zoom and canvas structure ─────────────────────────────────────────────
    private Group zoomGroup;
    private final Delta dragDelta = new Delta();

    private ListView<Poi> map_listview;

    @FXML
    private ScrollPane map_scrollpane;
    @FXML
    private Slider zoom_slider;
    @FXML
    private Pane paneCarta;

    // ── Profile / top menu ───────────────────────────────────────────────────
    @FXML
    private MenuButton mbPerfil;
    @FXML
    private MenuItem miEditarPerfil;
    @FXML
    private MenuItem menuItemEliminarMarcaTool;
    @FXML
    private MenuItem menuItemLimpiarCartaTool;

    // ── Color pickers and arc/text controls ──────────────────────────────────
    @FXML
    private ColorPicker colorPickkerPunto;
    @FXML
    private ColorPicker colorPickerLinea;
    @FXML
    private ColorPicker colorPickerArco;
    @FXML
    private TextField textFieldRadioArco;
    @FXML
    private TextField textFieldTamanoTexto;
    @FXML
    private ColorPicker colorPickerTexto;
    @FXML
    private ColorPicker colorPickerEdicion;

    // ── Toolbar menus ─────────────────────────────────────────────────────────
    @FXML
    private MenuItem menuItemIniciarLinea;
    @FXML
    private MenuItem menuItemTransportador;
    @FXML
    private Menu menuMarcarPunto;
    @FXML
    private MenuItem menuItemIniciarPunto;
    @FXML
    private MenuButton mbMarcarPunto;
    @FXML
    private MenuItem menuItemMarcaCirculo;
    @FXML
    private MenuItem menuItemMarcaCruz;
    @FXML
    private MenuItem menuItemMarcaAsterisco;
    @FXML
    private MenuItem menuItemMarcaEstrella;
    @FXML
    private Menu menuTrazarLinea;
    @FXML
    private MenuItem menuGrosorLinea;
    @FXML
    private Menu menuTrazarArco;
    @FXML
    private MenuItem menuItemIniciarArco;
    @FXML
    private MenuItem menuItemRadioLibre;
    @FXML
    private MenuItem menuItemRadioFijo;
    @FXML
    private Menu menuAnotarTexto;
    @FXML
    private MenuItem menuItemAnotarTexto;
    @FXML
    private Menu menuColor;
    @FXML
    private MenuItem menuItemEditarColor;
    @FXML
    private Menu menuEliminarMarca;
    @FXML
    private Menu menuLimpiarCarta;
    @FXML
    private MenuItem btnRegla;
    @FXML
    private MenuItem menuItemLyL;
    @FXML
    private Slider sliderGrosorLinea;
    @FXML
    private Slider sliderGrosorArco;
    @FXML
    private MenuItem miCerrarAplicacion;
    @FXML
    private Menu menuHerramienta;
    @FXML
    private Menu menuAyuda;

    // ── Icons ─────────────────────────────────────────────────────────────────
    @FXML
    private ImageView ivPerfil;
    @FXML
    private ImageView ivMenuMarcarPunto;
    @FXML
    private ImageView ivMenuTrazarLinea;
    @FXML
    private ImageView ivMenuTrazarArco;
    @FXML
    private ImageView ivMenuAnotarTexto;
    @FXML
    private ImageView ivMenuColor;
    @FXML
    private ImageView ivMenuEliminarMarca;
    @FXML
    private ImageView ivMenuLimpiarCarta;
    @FXML
    private ImageView ivMenuAyuda;
    @FXML
    private ImageView ivMenuHerramienta;

    // ── Problem area ──────────────────────────────────────────────────────────
    @FXML
    private TextArea taProblem;
    @FXML
    private MenuItem estadisticasButton;
    @FXML
    private Button buttonElegirProblema;
    @FXML
    private RadioButton buttonA;
    @FXML
    private ToggleGroup grupoRespuestas;
    @FXML
    private RadioButton buttonB;
    @FXML
    private RadioButton buttonC;
    @FXML
    private RadioButton buttonD;
    @FXML
    private Button buttonTerminarProblema;

    // ── Root layout ───────────────────────────────────────────────────────────
    @FXML
    private BorderPane borderPane;

    // ── Live line preview ─────────────────────────────────────────────────────
    private Group previewLineGroup;
    private EventHandler<MouseEvent> lineDragHandler;

    // ── Tooltip HBoxes ─────────────────────────────────────────────────────────
    @FXML
    private HBox hboxMarcarPunto;
    @FXML
    private HBox hboxTrazarLinea;
    @FXML
    private HBox hboxTrazarArco;
    @FXML
    private HBox hboxAnotarTexto;
    @FXML
    private HBox hboxColor;
    @FXML
    private HBox hboxEliminarMarca;
    @FXML
    private HBox hboxLimpiarCarta;
    @FXML
    private HBox hboxHerramienta;
    @FXML
    private HBox hboxAyuda;

    // ── Active tool state ─────────────────────────────────────────────────────
    private enum Tool {
        NONE, POINT, LINE, ARC, TEXT, EDIT_COLOR, DELETE, COMPASS, RULER, PROTRACTOR, LATLON_MARKER, REGLA_VISUAL
    }

    private Tool currentTool = Tool.NONE;
    private boolean markingEnabled = false;

    // ── Point tool ────────────────────────────────────────────────────────────
    private Color colorSeleccionado = Color.RED;
    private String tipoPunto = "circulo";

    // ── Line tool ─────────────────────────────────────────────────────────────
    private boolean lineStarted = false;
    private double lineStartX, lineStartY;
    private Color colorLinea = Color.RED;
    private double grosorLinea = 4;

    // ── Arc tool ──────────────────────────────────────────────────────────────
    private boolean arcStarted = false;
    private double arcCenterX, arcCenterY;
    private Color colorArco = Color.RED;
    private double grosorArco = 4;
    private double radioFijo = 30;

    // ── Text tool ─────────────────────────────────────────────────────────────
    private double tamanoTexto = 12;
    private Color colorTexto = Color.RED;
    private String textoPendiente;

    // ── Color-edit tool ───────────────────────────────────────────────────────
    private Color colorEdicion = Color.BLACK;

    // ── Visual tools ──────────────────────────────────────────────────────────
    private Point2D compasP1, compasP2;
    private Point2D rulerP1, rulerP2;
    private ImageView ivOverlay;
    private Pane reglaContainer;
    private Group previewArcGroup;
    private EventHandler<MouseEvent> arcDragHandler;
    private Arc previewArc;
    private ImageView ivRegla;
    private Stage rotationStage;

    // ── Arc mode ──────────────────────────────────────────────────────────────
    private enum ModoArco {
        RADIO_FIJO, RADIO_LIBRE
    }

    private ModoArco modoArco = ModoArco.RADIO_FIJO;

    // ── User and navigation ───────────────────────────────────────────────────
    private Navigation nav;
    private String nick, email, pass;
    private Image avatar;
    private LocalDate birthday;
    private User user;

    // ── Problem / session stats ───────────────────────────────────────────────
    private List<Answer> listAnswers;
    private Problem problema;
    private List<model.Session> listSesions;
    private int hits;
    private int faults;

    // ─────────────────────────────────────────────────────────────────────────
    // Data injection from the login screen
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Injects the authenticated user and their profile data.
     * Must be called immediately after the FXML controller is retrieved from the
     * loader.
     */
    public void initUser(User user, String nick, String email, String pass,
            Image avatar, LocalDate birthday) {
        this.user = user;
        this.nick = nick;
        this.email = email;
        this.pass = pass;
        this.avatar = avatar;
        this.birthday = birthday;
        ivPerfil.imageProperty().setValue(user.getAvatar());
    }

    /**
     * Pre-loads a specific problem directly into the work area.
     *
     * @param problem the problem to display
     * @param index   the zero-based index of this problem in the full list
     */
    public void initProblema(Problem problem, int index) {
        this.problema = problem;
        taProblem.setText(problema.getText());
    }

    /**
     * Seeds the hit/fault counters carried over from a previous session segment.
     *
     * @param hits   number of correct answers so far
     * @param faults number of incorrect answers so far
     */
    public void initSes(int hits, int faults) {
        this.hits = hits;
        this.faults = faults;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helper inner class
    // ─────────────────────────────────────────────────────────────────────────

    /** Holds x/y offsets used during mouse-drag operations. */
    private static class Delta {
        double x, y;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Initialization
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            SqliteConnection sqlite = new SqliteConnection();
            sqlite.connectSqlite("data.db");
        } catch (SQLException ex) {
            Logger.getLogger(FXMLTrabajoController.class.getName()).log(Level.SEVERE, null, ex);
        }

        buttonTerminarProblema.setDisable(true);

        try {
            nav = Navigation.getInstance();
        } catch (NavDAOException ex) {
            Logger.getLogger(FXMLTrabajoController.class.getName()).log(Level.SEVERE, null, ex);
        }

        buttonA.setVisible(false);
        buttonB.setVisible(false);
        buttonC.setVisible(false);
        buttonD.setVisible(false);

        sliderGrosorArco.setShowTickLabels(true);
        sliderGrosorArco.setShowTickMarks(true);

        paneCarta.setPrefSize(2500, 1700);
        map_scrollpane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        map_scrollpane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        setupZoom();
        setupControls();
        setupPaneClickHandler();

        taProblem.setWrapText(true);

        // Line thickness slider
        sliderGrosorLinea.setMin(1);
        sliderGrosorLinea.setMax(10);
        sliderGrosorLinea.setBlockIncrement(1);
        sliderGrosorLinea.setMajorTickUnit(1);
        sliderGrosorLinea.setSnapToTicks(true);
        sliderGrosorLinea.setValue(grosorLinea);
        sliderGrosorLinea.valueProperty().addListener((obs, o, nval) -> grosorLinea = nval.intValue());

        // Arc thickness slider
        sliderGrosorArco.setMin(1);
        sliderGrosorArco.setMax(10);
        sliderGrosorArco.setBlockIncrement(1);
        sliderGrosorArco.setMajorTickUnit(1);
        sliderGrosorArco.setSnapToTicks(true);
        sliderGrosorArco.setValue(grosorArco);
        sliderGrosorArco.valueProperty().addListener((obs, o, nval) -> grosorArco = nval.intValue());

        Platform.runLater(() -> {
            colorearTickLabelsAzul(sliderGrosorLinea);
            colorearTickLabelsAzul(sliderGrosorArco);
        });

        textFieldRadioArco.setText(String.format("%.0f", radioFijo));
        textFieldTamanoTexto.setText(String.format("%.0f", tamanoTexto));

        // Protractor overlay setup
        Image rawTransportador = new Image(getClass().getResourceAsStream("/resources/transportador.png"));
        Image overlayTransportador = makeOverlayPreserveGray(rawTransportador);
        ivOverlay = new ImageView(overlayTransportador);
        ivOverlay.setMouseTransparent(false);
        ivOverlay.setPickOnBounds(true);
        ivOverlay.setVisible(false);
        paneCarta.getChildren().add(ivOverlay);

        final Delta dragDeltaOverlay = new Delta();

        ivOverlay.setOnMousePressed(e -> {
            Point2D mouse = paneCarta.sceneToLocal(e.getSceneX(), e.getSceneY());
            double overlayWidth = ivOverlay.getBoundsInLocal().getWidth();
            double overlayHeight = ivOverlay.getBoundsInLocal().getHeight();
            ivOverlay.setLayoutX(mouse.getX() - overlayWidth / 2);
            ivOverlay.setLayoutY(mouse.getY() - overlayHeight / 2);
            ivOverlay.setVisible(true);
            dragDeltaOverlay.x = mouse.getX() - ivOverlay.getLayoutX();
            dragDeltaOverlay.y = mouse.getY() - ivOverlay.getLayoutY();
            e.consume();
        });

        ivOverlay.setOnMouseDragged(e -> {
            Point2D mouse = paneCarta.sceneToLocal(e.getSceneX(), e.getSceneY());
            double candidateX = mouse.getX() - dragDeltaOverlay.x;
            double candidateY = mouse.getY() - dragDeltaOverlay.y;
            double overlayWidth = ivOverlay.getBoundsInLocal().getWidth();
            double overlayHeight = ivOverlay.getBoundsInLocal().getHeight();
            double maxX = paneCarta.getWidth() - overlayWidth;
            double maxY = paneCarta.getHeight() - overlayHeight;
            ivOverlay.setLayoutX(Math.max(0, Math.min(maxX, candidateX)));
            ivOverlay.setLayoutY(Math.max(0, Math.min(maxY, candidateY)));
            e.consume();
        });

        ivOverlay.setOnMouseReleased(e -> {
            resetModes();
            markingEnabled = false;
            currentTool = Tool.NONE;
            e.consume();
        });

        // Ruler overlay setup
        Pane rulerPane = new Pane();
        rulerPane.setPickOnBounds(true);
        rulerPane.setStyle("-fx-background-color: rgba(0,0,0,0);");

        Image rulerImage = new Image(getClass().getResourceAsStream("/resources/regla123.png"));
        ivRegla = new ImageView(rulerImage);
        ivRegla.setOpacity(0.85);
        ivRegla.setScaleX(0.8);
        ivRegla.setScaleY(0.8);
        ivRegla.setMouseTransparent(true);

        rulerPane.getChildren().add(ivRegla);
        paneCarta.getChildren().add(rulerPane);
        rulerPane.setVisible(false);

        final Delta dragDeltaRegla = new Delta();

        rulerPane.setOnMousePressed(e -> {
            Point2D mouse = paneCarta.sceneToLocal(e.getSceneX(), e.getSceneY());
            dragDeltaRegla.x = mouse.getX() - rulerPane.getLayoutX();
            dragDeltaRegla.y = mouse.getY() - rulerPane.getLayoutY();
            e.consume();
        });

        rulerPane.setOnMouseDragged(event -> {
            Point2D mouse = paneCarta.sceneToLocal(event.getSceneX(), event.getSceneY());
            double candidateX = mouse.getX() - dragDeltaRegla.x;
            double candidateY = mouse.getY() - dragDeltaRegla.y;
            Bounds bounds = getBoundsAt(rulerPane, candidateX, candidateY);
            double maxX = paneCarta.getWidth();
            double maxY = paneCarta.getHeight();

            if (bounds.getMinX() < 0)
                candidateX += (0 - bounds.getMinX());
            if (bounds.getMinY() < 0)
                candidateY += (0 - bounds.getMinY());
            if (bounds.getMaxX() > maxX)
                candidateX -= (bounds.getMaxX() - maxX);
            if (bounds.getMaxY() > maxY)
                candidateY -= (bounds.getMaxY() - maxY);

            rulerPane.setLayoutX(candidateX);
            rulerPane.setLayoutY(candidateY);
            event.consume();
        });

        rulerPane.setOnScroll(e -> {
            rulerPane.setRotate(rulerPane.getRotate() + e.getDeltaY() / 10);
            e.consume();
        });

        rulerPane.setOnMouseReleased(e -> e.consume());

        this.reglaContainer = rulerPane;

        // Tooltips
        instalarTooltip(hboxMarcarPunto, "Realizar marcas");
        instalarTooltip(hboxTrazarLinea, "Trazar linea");
        instalarTooltip(hboxTrazarArco, "Trazar arco");
        instalarTooltip(hboxAnotarTexto, "Escribir texto");
        instalarTooltip(hboxColor, "Cambiar de color cualquier elemento");
        instalarTooltip(hboxEliminarMarca, "Eliminar cualquier elemento");
        instalarTooltip(hboxLimpiarCarta, "Limpiar la carta de elementos");
        instalarTooltip(hboxAyuda, "Ayuda");
        instalarTooltip(hboxHerramienta, "Regla, transportador y longitud y latitud");

        // Lat/Lon crosshair line placement
        paneCarta.setOnMousePressed(e -> {
            if (currentTool == Tool.LATLON_MARKER) {
                double x = e.getX(), y = e.getY();
                double ancho = 200000;
                double alto = 12000;

                double startXH = Math.max(0, x - ancho / 2);
                double endXH = Math.min(paneCarta.getWidth(), x + ancho / 2);
                double startYV = Math.max(0, y - alto / 2);
                double endYV = Math.min(paneCarta.getHeight(), y + alto / 2);

                Line horizontalLine = new Line(startXH, y, endXH, y);
                Line verticalLine = new Line(x, startYV, x, endYV);
                horizontalLine.setStroke(Color.RED);
                verticalLine.setStroke(Color.RED);
                horizontalLine.setStrokeWidth(2);
                verticalLine.setStrokeWidth(2);
                horizontalLine.setId("latlonLine");
                verticalLine.setId("latlonLine");

                Group latLonGroup = new Group(horizontalLine, verticalLine);
                latLonGroup.setId("latlonGroup");
                paneCarta.getChildren().add(latLonGroup);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UI utilities
    // ─────────────────────────────────────────────────────────────────────────

    /** Attaches a long-lived tooltip to the given HBox toolbar item. */
    private void instalarTooltip(HBox hbox, String text) {
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDuration(Duration.seconds(1000));
        tooltip.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-weight: normal;");
        Tooltip.install(hbox, tooltip);
    }

    /**
     * Configures the zoom slider and wraps the chart content in a {@link Group}
     * so that scale transforms do not shift the scroll-pane viewport.
     */
    private void setupZoom() {
        zoom_slider.setMin(0.2);
        zoom_slider.setMax(1.5);
        zoom_slider.setValue(0.75);
        zoom_slider.valueProperty().addListener((obs, ov, nv) -> zoom(nv.doubleValue()));

        Group content = new Group();
        zoomGroup = new Group();
        content.getChildren().add(zoomGroup);
        zoomGroup.getChildren().add(map_scrollpane.getContent());
        map_scrollpane.setContent(content);
    }

    /**
     * Applies a uniform scale to the zoom group while preserving the scroll
     * position.
     */
    private void zoom(double scale) {
        double h = map_scrollpane.getHvalue();
        double v = map_scrollpane.getVvalue();
        zoomGroup.setScaleX(scale);
        zoomGroup.setScaleY(scale);
        map_scrollpane.setHvalue(h);
        map_scrollpane.setVvalue(v);
    }

    /**
     * Wires all color-picker and text-field controls to their backing state
     * variables.
     * Text fields apply their value both on Enter and on focus-lost.
     */
    private void setupControls() {
        colorPickkerPunto.setOnAction(e -> colorSeleccionado = colorPickkerPunto.getValue());
        colorPickerLinea.setOnAction(e -> colorLinea = colorPickerLinea.getValue());
        colorPickerArco.setOnAction(e -> colorArco = colorPickerArco.getValue());
        colorPickerTexto.setOnAction(e -> colorTexto = colorPickerTexto.getValue());
        colorPickerEdicion.setOnAction(e -> colorEdicion = colorPickerEdicion.getValue());

        textFieldTamanoTexto.setOnAction(e -> {
            try {
                tamanoTexto = Double.parseDouble(textFieldTamanoTexto.getText());
            } catch (NumberFormatException ex) {
                textFieldTamanoTexto.setText(String.format("%.0f", tamanoTexto));
            }
        });
        textFieldTamanoTexto.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                try {
                    tamanoTexto = Double.parseDouble(textFieldTamanoTexto.getText());
                } catch (NumberFormatException ex) {
                    textFieldTamanoTexto.setText(String.format("%.0f", tamanoTexto));
                }
            }
        });
        textFieldRadioArco.setOnAction(e -> {
            try {
                radioFijo = Double.parseDouble(textFieldRadioArco.getText());
            } catch (NumberFormatException ex) {
                textFieldRadioArco.setText(String.format("%.0f", radioFijo));
            }
        });
    }

    /**
     * Registers the central mouse-click handler on the chart pane.
     * Right-click always resets the active tool; left-click dispatches to the
     * appropriate drawing or measurement handler based on {@link #currentTool}.
     */
    private void setupPaneClickHandler() {
        paneCarta.setOnMouseClicked(evt -> {
            if (evt.getButton() == MouseButton.SECONDARY) {
                resetModes();
                return;
            }
            if (!markingEnabled)
                return;
            double x = evt.getX(), y = evt.getY();

            switch (currentTool) {
                case POINT -> drawPoint(x, y);
                case LINE -> handleLineClick(evt);
                case ARC -> handleArcClick(evt);
                case TEXT -> handleTextClick(x, y);

                case EDIT_COLOR -> {
                    Node n = (Node) evt.getTarget();
                    if (n != paneCarta)
                        applyColor(n, colorEdicion);
                }

                case DELETE -> {
                    Node n = (Node) evt.getTarget();
                    if (n instanceof Shape || n instanceof Text) {
                        if (n.getParent() instanceof Group)
                            paneCarta.getChildren().remove(n.getParent());
                        else
                            paneCarta.getChildren().remove(n);
                    }
                }

                case COMPASS -> measureCompass(x, y);

                case RULER, REGLA_VISUAL -> {
                    double clickX = evt.getX(), clickY = evt.getY();
                    if (ivRegla != null) {
                        paneCarta.getChildren().remove(ivRegla);
                        ivRegla = null;
                    }

                    Image rulerImg = new Image(getClass().getResourceAsStream("/resources/regla123.png"));
                    ivRegla = new ImageView(rulerImg);
                    ivRegla.setFitWidth(rulerImg.getWidth() * (1.0 / 6.0));
                    ivRegla.setPreserveRatio(true);
                    ivRegla.setOpacity(1.0);
                    ivRegla.setMouseTransparent(false);
                    ivRegla.setPickOnBounds(true);
                    paneCarta.getChildren().add(ivRegla);

                    Platform.runLater(() -> {
                        ivRegla.setLayoutX(clickX - ivRegla.getBoundsInLocal().getWidth() / 2);
                        ivRegla.setLayoutY(clickY - ivRegla.getBoundsInLocal().getHeight() / 2);
                    });
                    setupReglaDragHandlers(ivRegla, paneCarta);
                    openRotationWindowNormal();
                    resetModes();
                }

                case PROTRACTOR -> {
                    ivOverlay.setLayoutX(x - ivOverlay.getImage().getWidth() / 2);
                    ivOverlay.setLayoutY(y - ivOverlay.getImage().getHeight() / 2);
                    ivOverlay.setVisible(true);
                }

                case LATLON_MARKER -> resetModes();
                default -> {
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Drawing helpers
    // ─────────────────────────────────────────────────────────────────────────

    /** Draws the selected point symbol at the given canvas coordinates. */
    private void drawPoint(double x, double y) {
        switch (tipoPunto) {
            case "estrella" -> {
                Text t = new Text(x - 6, y + 6, "★");
                t.setFill(colorSeleccionado);
                t.setStyle("-fx-font-size: 15.4px;");
                paneCarta.getChildren().add(t);
            }
            case "asterisco" -> {
                Text t = new Text(x - 4, y + 6, "*");
                t.setFill(colorSeleccionado);
                t.setStyle("-fx-font-size: 35px;");
                paneCarta.getChildren().add(t);
            }
            case "cruz" -> {
                Text t = new Text(x - 4, y + 6, "+");
                t.setFill(colorSeleccionado);
                t.setStyle("-fx-font-size: 26.6px;");
                paneCarta.getChildren().add(t);
            }
            default -> {
                Circle c = new Circle(x, y, 5);
                c.setFill(colorSeleccionado);
                paneCarta.getChildren().add(c);
            }
        }
    }

    /**
     * Applies a blue tint to the tick labels of a slider for visual consistency.
     * Must be called after CSS has been applied (e.g. inside
     * {@link Platform#runLater}).
     */
    public void colorearTickLabelsAzul(Slider slider) {
        slider.applyCss();
        for (Node node : slider.lookupAll(".tick-label")) {
            if (node instanceof Text t)
                t.setFill(Color.web("#0d47a1"));
        }
    }

    /**
     * Two-phase line drawing: first click sets the start point and shows a live
     * preview; second click finalises the line on the canvas.
     */
    private void handleLineClick(MouseEvent evt) {
        double x = evt.getX(), y = evt.getY();
        if (!lineStarted) {
            lineStartX = x;
            lineStartY = y;
            lineStarted = true;
            Line line = new Line(lineStartX, lineStartY, x, y);
            line.setStroke(colorLinea);
            line.setStrokeWidth(grosorLinea);
            Line h1 = new Line(lineStartX - 5, lineStartY, lineStartX + 5, lineStartY);
            Line v1 = new Line(lineStartX, lineStartY - 5, lineStartX, lineStartY + 5);
            Line h2 = new Line(x - 5, y, x + 5, y);
            Line v2 = new Line(x, y - 5, x, y + 5);
            for (Line l : List.of(h1, v1, h2, v2))
                l.setStroke(colorLinea);
            previewLineGroup = new Group(line, h1, v1, h2, v2);
            paneCarta.getChildren().add(previewLineGroup);
            lineDragHandler = mv -> {
                double ex = mv.getX(), ey = mv.getY();
                line.setEndX(ex);
                line.setEndY(ey);
                h2.setStartX(ex - 5);
                h2.setStartY(ey);
                h2.setEndX(ex + 5);
                h2.setEndY(ey);
                v2.setStartX(ex);
                v2.setStartY(ey - 5);
                v2.setEndX(ex);
                v2.setEndY(ey + 5);
            };
            paneCarta.addEventFilter(MouseEvent.MOUSE_MOVED, lineDragHandler);
        } else {
            paneCarta.removeEventFilter(MouseEvent.MOUSE_MOVED, lineDragHandler);
            lineStarted = false;
            resetModes();
        }
    }

    /**
     * Handles arc placement. Delegates to fixed-radius or free-radius sub-handler
     * depending on {@link #modoArco}.
     */
    private void handleArcClick(MouseEvent evt) {
        double x = evt.getX(), y = evt.getY();
        if (modoArco == ModoArco.RADIO_FIJO) {
            if (!arcStarted) {
                arcCenterX = x;
                arcCenterY = y;
                arcStarted = true;
            } else {
                try {
                    radioFijo = Double.parseDouble(textFieldRadioArco.getText());
                } catch (NumberFormatException ex) {
                    textFieldRadioArco.setText(String.format("%.0f", radioFijo));
                }
                Arc arc = new Arc(arcCenterX, arcCenterY, radioFijo, radioFijo, 0, 180);
                arc.setType(ArcType.OPEN);
                arc.setStroke(colorArco);
                arc.setStrokeWidth(grosorArco);
                arc.setFill(Color.TRANSPARENT);
                Line h1 = new Line(arcCenterX + radioFijo - 5, arcCenterY, arcCenterX + radioFijo + 5, arcCenterY);
                Line v1 = new Line(arcCenterX + radioFijo, arcCenterY - 5, arcCenterX + radioFijo, arcCenterY + 5);
                Line h2 = new Line(arcCenterX - radioFijo - 5, arcCenterY, arcCenterX - radioFijo + 5, arcCenterY);
                Line v2 = new Line(arcCenterX - radioFijo, arcCenterY - 5, arcCenterX - radioFijo, arcCenterY + 5);
                for (Line cross : List.of(h1, v1, h2, v2))
                    cross.setStroke(colorArco);
                paneCarta.getChildren().add(new Group(arc, h1, v1, h2, v2));
                arcStarted = false;
                resetModes();
            }
        } else if (modoArco == ModoArco.RADIO_LIBRE) {
            handleArcLibre(evt);
        }
    }

    /**
     * Free-radius arc: first click sets centre; mouse movement previews radius;
     * second click commits the arc to the canvas.
     */
    private void handleArcLibre(MouseEvent evt) {
        double x = evt.getX(), y = evt.getY();
        if (!arcStarted) {
            arcCenterX = x;
            arcCenterY = y;
            arcStarted = true;
            previewArc = new Arc(arcCenterX, arcCenterY, 1, 1, 0, 180);
            previewArc.setType(ArcType.OPEN);
            previewArc.setStroke(colorArco);
            previewArc.setStrokeWidth(grosorArco);
            previewArc.setFill(Color.TRANSPARENT);
            Line h1 = new Line(), v1 = new Line(), h2 = new Line(), v2 = new Line();
            for (Line l : List.of(h1, v1, h2, v2))
                l.setStroke(colorArco);
            previewArcGroup = new Group(previewArc, h1, v1, h2, v2);
            paneCarta.getChildren().add(previewArcGroup);
            arcDragHandler = mv -> {
                double ex = mv.getX(), ey = mv.getY();
                double r = Math.hypot(ex - arcCenterX, ey - arcCenterY);
                double maxR = Math.min(Math.min(arcCenterX, paneCarta.getWidth() - arcCenterX),
                        Math.min(arcCenterY, paneCarta.getHeight() - arcCenterY));
                if (r > maxR)
                    r = maxR;
                previewArc.setRadiusX(r);
                previewArc.setRadiusY(r);
                double rx = arcCenterX + r, lx = arcCenterX - r, cy = arcCenterY;
                h1.setStartX(rx - 5);
                h1.setEndX(rx + 5);
                h1.setStartY(cy);
                h1.setEndY(cy);
                v1.setStartX(rx);
                v1.setEndX(rx);
                v1.setStartY(cy - 5);
                v1.setEndY(cy + 5);
                h2.setStartX(lx - 5);
                h2.setEndX(lx + 5);
                h2.setStartY(cy);
                h2.setEndY(cy);
                v2.setStartX(lx);
                v2.setEndX(lx);
                v2.setStartY(cy - 5);
                v2.setEndY(cy + 5);
            };
            paneCarta.addEventFilter(MouseEvent.MOUSE_MOVED, arcDragHandler);
        } else {
            paneCarta.removeEventFilter(MouseEvent.MOUSE_MOVED, arcDragHandler);
            arcStarted = false;
            previewArc = null;
            previewArcGroup = null;
            resetModes();
        }
    }

    /** Makes a {@link Text} node draggable within the chart pane. */
    private void makeDraggable(Text txt) {
        final double[] offset = new double[2];
        txt.setOnMousePressed(e -> {
            Point2D loc = paneCarta.sceneToLocal(e.getSceneX(), e.getSceneY());
            offset[0] = loc.getX() - txt.getX();
            offset[1] = loc.getY() - txt.getY();
            e.consume();
        });
        txt.setOnMouseDragged(e -> {
            Point2D loc = paneCarta.sceneToLocal(e.getSceneX(), e.getSceneY());
            txt.setX(loc.getX() - offset[0]);
            txt.setY(loc.getY() - offset[1]);
            e.consume();
        });
    }

    /**
     * Converts a source image into a chart overlay by making near-white/transparent
     * pixels fully transparent while preserving all other pixels at full opacity.
     *
     * @param src the source image (e.g. a scanned protractor)
     * @return a new image ready to be placed over the chart
     */
    private Image makeOverlayPreserveGray(Image src) {
        int w = (int) src.getWidth(), h = (int) src.getHeight();
        javafx.scene.image.WritableImage dst = new javafx.scene.image.WritableImage(w, h);
        javafx.scene.image.PixelReader pr = src.getPixelReader();
        javafx.scene.image.PixelWriter pw = dst.getPixelWriter();
        for (int row = 0; row < h; row++) {
            for (int col = 0; col < w; col++) {
                Color c = pr.getColor(col, row);
                pw.setColor(col, row,
                        (c.getOpacity() > 0.02 && c.getBrightness() < 0.95)
                                ? new Color(c.getRed(), c.getGreen(), c.getBlue(), 1.0)
                                : Color.TRANSPARENT);
            }
        }
        return dst;
    }

    /**
     * Places the pending text annotation at the clicked position and resets the
     * tool.
     */
    private void handleTextClick(double x, double y) {
        if (textoPendiente != null) {
            Text txt = new Text(x, y, textoPendiente);
            txt.setFill(colorTexto);
            txt.setFont(Font.font(tamanoTexto));
            makeDraggable(txt);
            paneCarta.getChildren().add(txt);
            textoPendiente = null;
        }
        resetModes();
    }

    /**
     * Applies a new colour to the given node or to every child of a {@link Group}.
     * Arc nodes retain a transparent fill so only the stroke is recoloured.
     *
     * @param node  the target node (may be a Group)
     * @param color the colour to apply
     */
    private void applyColor(Node node, Color color) {
        if (node.getParent() instanceof Group group) {
            for (Node child : group.getChildren()) {
                if (child instanceof Arc arc) {
                    arc.setStroke(color);
                    arc.setFill(Color.TRANSPARENT);
                } else if (child instanceof Shape s) {
                    s.setStroke(color);
                    s.setFill(color);
                } else if (child instanceof Text t) {
                    t.setFill(color);
                }
            }
        } else {
            if (node instanceof Arc arc) {
                arc.setStroke(color);
                arc.setFill(Color.TRANSPARENT);
            } else if (node instanceof Shape s) {
                s.setStroke(color);
                s.setFill(color);
            } else if (node instanceof Text t) {
                t.setFill(color);
            }
        }
    }

    /**
     * Two-phase compass measurement: first click records point A; second click
     * draws the line and labels it with the pixel distance.
     */
    private void measureCompass(double x, double y) {
        if (compasP1 == null) {
            compasP1 = new Point2D(x, y);
        } else {
            compasP2 = new Point2D(x, y);
            double dist = compasP1.distance(compasP2);
            Line line = new Line(compasP1.getX(), compasP1.getY(), compasP2.getX(), compasP2.getY());
            paneCarta.getChildren().add(line);
            Text label = new Text((compasP1.getX() + compasP2.getX()) / 2,
                    (compasP1.getY() + compasP2.getY()) / 2,
                    String.format("%.1f", dist));
            makeDraggable(label);
            paneCarta.getChildren().add(label);
            compasP1 = compasP2 = null;
        }
    }

    /**
     * Opens (or brings to front) the ruler rotation floating window.
     * The window is always-on-top so it remains accessible while drawing.
     */
    private void openRotationWindowNormal() {
        try {
            if (rotationStage == null || !rotationStage.isShowing()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/RotarRegla.fxml"));
                Parent root = loader.load();
                rotationStage = new Stage();
                rotationStage.setTitle("Rotar Regla");
                rotationStage.setScene(new Scene(root));
                rotationStage.setAlwaysOnTop(true);
                rotationStage.initOwner(paneCarta.getScene().getWindow());
                RotarReglaController rotCtrl = loader.getController();
                rotCtrl.setRuleImageView(ivRegla);
                rotationStage.show();
            } else {
                rotationStage.toFront();
            }
        } catch (IOException e) {
            Logger.getLogger(FXMLTrabajoController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Attaches drag handlers to a ruler ImageView so the user can reposition it
     * freely inside the chart pane. Right-click release hides the ruler.
     *
     * @param rulerView the ruler ImageView to make draggable
     * @param chartPane the parent pane defining the drag boundary
     */
    public void setupReglaDragHandlers(ImageView rulerView, Pane chartPane) {
        rulerView.setOnMousePressed(ev -> {
            Point2D mouse = chartPane.sceneToLocal(ev.getSceneX(), ev.getSceneY());
            dragDelta.x = mouse.getX() - rulerView.getLayoutX();
            dragDelta.y = mouse.getY() - rulerView.getLayoutY();
            ev.consume();
        });
        rulerView.setOnMouseDragged(ev -> {
            Point2D mouse = chartPane.sceneToLocal(ev.getSceneX(), ev.getSceneY());
            double cx = mouse.getX() - dragDelta.x;
            double cy = mouse.getY() - dragDelta.y;
            rulerView.setLayoutX(cx);
            rulerView.setLayoutY(cy);
            Bounds b = rulerView.getBoundsInParent();
            if (b.getMinX() < 0)
                cx += (0 - b.getMinX());
            if (b.getMinY() < 0)
                cy += (0 - b.getMinY());
            if (b.getMaxX() > chartPane.getWidth())
                cx -= (b.getMaxX() - chartPane.getWidth());
            if (b.getMaxY() > chartPane.getHeight())
                cy -= (b.getMaxY() - chartPane.getHeight());
            rulerView.setLayoutX(cx);
            rulerView.setLayoutY(cy);
            ev.consume();
        });
        rulerView.setOnMouseReleased(ev -> {
            if (ev.getButton() == MouseButton.SECONDARY) {
                rulerView.setVisible(false);
                resetModes();
                markingEnabled = false;
                currentTool = Tool.NONE;
                ev.consume();
            }
        });
    }

    /**
     * Temporarily moves a node to a candidate position to read its bounds in
     * parent,
     * then restores it. Used for boundary-clamping calculations during drag events.
     */
    private Bounds getBoundsAt(Node node, double layoutX, double layoutY) {
        double oldX = node.getLayoutX(), oldY = node.getLayoutY();
        node.setLayoutX(layoutX);
        node.setLayoutY(layoutY);
        Bounds bounds = node.getBoundsInParent();
        node.setLayoutX(oldX);
        node.setLayoutY(oldY);
        return bounds;
    }

    /** Resets all active-tool state back to the idle (no-operation) mode. */
    private void resetModes() {
        currentTool = Tool.NONE;
        markingEnabled = false;
        lineStarted = false;
        arcStarted = false;
        compasP1 = compasP2 = rulerP1 = rulerP2 = null;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // @FXML event handlers
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    public void seleccionarLatLonMarker(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.LATLON_MARKER;
    }

    /**
     * Closes the current session by opening the session-end dialog to persist
     * stats.
     */
    @FXML
    private void onCerrarAplicacion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VentanaCerrarSesion.fxml"));
        Parent root = loader.load();
        CerrarSesionController ctrl = loader.getController();
        ctrl.initSesion(hits, faults);
        ctrl.initUser(user, nick, email, pass, avatar, birthday);
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Cerrar Sesión");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo1.png")));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(500);
        stage.setMinHeight(480);
        stage.show();
    }

    @FXML
    private void showPosition(MouseEvent event) {
        // Reserved for future coordinate-display implementation.
    }

    /** Aggregates all session hits and faults and opens the statistics window. */
    @FXML
    private void onEstadisticas(ActionEvent event) throws IOException {
        int totalHits = 0, totalFaults = 0;
        listSesions = user.getSessions();
        for (model.Session s : listSesions) {
            totalHits += s.getHits();
            totalFaults += s.getFaults();
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/Estadisticas.fxml"));
        Parent root = loader.load();
        EstadisticasController statsCtrl = loader.getController();
        statsCtrl.initUser(nick, email, pass, avatar, birthday);
        statsCtrl.initEstadisticas(totalHits, totalFaults);

        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo1.png")));
        stage.setScene(new Scene(root, 650, 500));
        stage.setTitle("Estadísticas");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(660);
        stage.setMinHeight(400);
        stage.show();
    }

    /** Opens the problem picker dialog and loads the confirmed selection. */
    @FXML
    private void elegirProblemaOnAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/ElegirProblema.fxml"));
        Parent root = loader.load();
        ElegirProblemaController problemCtrl = loader.getController();

        Stage stage = new Stage();
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo1.png")));
        stage.setScene(new Scene(root, 700, 370));
        stage.setTitle("Elegir Problema");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setMinWidth(600);
        stage.setMinHeight(300);
        stage.showAndWait();

        if (problemCtrl.isProblemSelected()) { // ← renamed call-site
            problema = problemCtrl.getProblem();
            listAnswers = problema.getAnswers();
            taProblem.setText(problema.getText());
            buttonA.setText(listAnswers.get(0).getText());
            buttonB.setText(listAnswers.get(1).getText());
            buttonC.setText(listAnswers.get(2).getText());
            buttonD.setText(listAnswers.get(3).getText());
            buttonA.setVisible(true);
            buttonB.setVisible(true);
            buttonC.setVisible(true);
            buttonD.setVisible(true);
        }
        buttonTerminarProblema.setDisable(false);
    }

    /**
     * Evaluates the chosen answer, updates hit/fault counters, and signals the
     * result.
     */
    @FXML
    private void terminarProblemaOnAction(ActionEvent event) {
        RadioButton selected = (grupoRespuestas.getSelectedToggle() instanceof RadioButton rb) ? rb : null;
        listAnswers = problema.getAnswers();

        if (selected != null) {
            String selectedText = selected.getText();
            int selectedIndex = -1;
            for (int i = 0; i < listAnswers.size(); i++) {
                if (listAnswers.get(i).getText().equals(selectedText)) {
                    selectedIndex = i;
                    break;
                }
            }
            if (selectedIndex != -1) {
                boolean correct = listAnswers.get(selectedIndex).getValidity();
                if (correct) {
                    taProblem.setText("");
                    buttonA.setVisible(false);
                    buttonB.setVisible(false);
                    buttonC.setVisible(false);
                    buttonD.setVisible(false);
                    hits++;
                    listAnswers = null;
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setTitle("Respuesta");
                    a.setHeaderText("ACIERTO!");
                    a.setContentText("Pulsa aceptar para continuar haciendo ejercicios");
                    a.show();
                } else {
                    faults++;
                }
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Advertencia");
            a.setHeaderText("¡Error!");
            a.setContentText("No has seleccionado ninguna respuesta");
            a.show();
        }
    }

    @FXML
    public void seleccionarHerramientaMarcarPunto(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.POINT;
    }

    @FXML
    public void seleccionarTrazarLinea(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.LINE;
    }

    @FXML
    public void seleccionarTrazarArco(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.ARC;
    }

    @FXML
    public void onMenuColorPicked(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.EDIT_COLOR;
        colorPickerEdicion.show();
    }

    @FXML
    public void seleccionarEliminarElemento(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.DELETE;
    }

    @FXML
    public void seleccionarLimpiarCarta(ActionEvent e) {
        paneCarta.getChildren().removeIf(n -> n instanceof Shape || n instanceof Text || n instanceof Group);
        resetModes();
    }

    public void seleccionarGrosorLinea(ActionEvent e) {
        grosorLinea = sliderGrosorLinea.getValue();
    }

    public void seleccionarGrosorArco(ActionEvent e) {
        grosorArco = sliderGrosorArco.getValue();
    }

    public void seleccionarGrosorMuyGruesoLinea(ActionEvent e) {
        grosorLinea = 6;
    }

    public void seleccionarGrosorGruesoLinea(ActionEvent e) {
        grosorLinea = 4;
    }

    public void seleccionarGrosorMedioLinea(ActionEvent e) {
        grosorLinea = 2;
    }

    public void seleccionarGrosorFinoLinea(ActionEvent e) {
        grosorLinea = 1;
    }

    public void seleccionarGrosorMuyGruesoArco(ActionEvent e) {
        grosorArco = 6;
    }

    public void seleccionarGrosorGruesoArco(ActionEvent e) {
        grosorArco = 4;
    }

    public void seleccionarGrosorMedioArco(ActionEvent e) {
        grosorArco = 2;
    }

    public void seleccionarGrosorFinoArco(ActionEvent e) {
        grosorArco = 1;
    }

    public void seleccionarCompas(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.COMPASS;
    }

    @FXML
    public void seleccionarRegla(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.RULER;
    }

    @FXML
    public void seleccionarTransportador(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.PROTRACTOR;
        ivOverlay.setVisible(true);
    }

    /**
     * Opens the profile edit dialog; refreshes the avatar image on a confirmed
     * save.
     */
    @FXML
    public void onEditarPerfil(ActionEvent e) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/PerfilPanel.fxml"));
        Parent root = loader.load();
        Stage dialog = new Stage();
        dialog.setTitle("Editar perfil");
        dialog.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo1.png")));
        dialog.initOwner(paneCarta.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);

        PerfilPanelController profileCtrl = loader.getController();
        profileCtrl.initUser(nick, email, pass, avatar, birthday);
        profileCtrl.populateFields(nick, email, pass, avatar, birthday); // ← renamed call-site

        dialog.setScene(new Scene(root));
        dialog.setMinWidth(500);
        dialog.setMinHeight(400);
        dialog.showAndWait();

        if (profileCtrl.isSaveConfirmed()) { // ← renamed call-site
            User updatedUser = profileCtrl.getUser();
            user = updatedUser;
            ivPerfil.setImage(updatedUser.getAvatar());
        }
    }

    /** Prompts for text input then activates the annotation-placement tool. */
    @FXML
    public void seleccionarAnotarTexto(ActionEvent e) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Anotar texto");
        dlg.setHeaderText("Introduce el texto:");
        dlg.getEditor().setPromptText("Tu texto aquí");
        Optional<String> result = dlg.showAndWait();
        result.ifPresent(s -> {
            textoPendiente = s;
            resetModes();
            markingEnabled = true;
            currentTool = Tool.TEXT;
        });
    }

    /** Adds a new POI at the Ctrl-clicked chart position via an input dialog. */
    @FXML
    private void addPoi(MouseEvent e) {
        if (!e.isControlDown())
            return;
        Dialog<Poi> dlg = new Dialog<>();
        dlg.setTitle("Nuevo POI");
        dlg.setHeaderText("Introduce un nuevo POI");
        Stage ds = (Stage) dlg.getDialogPane().getScene().getWindow();
        ds.getIcons().add(new Image(getClass().getResourceAsStream("/resources/logo.png")));
        ButtonType ok = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);
        TextField nameField = new TextField();
        nameField.setPromptText("Nombre del POI");
        TextArea descArea = new TextArea();
        descArea.setPromptText("Descripción...");
        descArea.setWrapText(true);
        descArea.setPrefRowCount(5);
        dlg.getDialogPane().setContent(new javafx.scene.layout.VBox(10,
                new Label("Nombre:"), nameField, new Label("Descripción:"), descArea));
        dlg.setResultConverter(
                btn -> btn == ok ? new Poi(nameField.getText().trim(), descArea.getText().trim(), 0, 0) : null);
        Optional<Poi> result = dlg.showAndWait();
        result.ifPresent(poi -> {
            poi.setPosition(zoomGroup.sceneToLocal(e.getSceneX(), e.getSceneY()));
            map_listview.getItems().add(poi);
        });
    }

    @FXML
    public void seleccionarMarcaCirculo(ActionEvent e) {
        tipoPunto = "circulo";
    }

    @FXML
    public void seleccionarMarcaEstrella(ActionEvent e) {
        tipoPunto = "estrella";
    }

    @FXML
    public void seleccionarMarcaAsterisco(ActionEvent e) {
        tipoPunto = "asterisco";
    }

    @FXML
    public void seleccionarMarcaCruz(ActionEvent e) {
        tipoPunto = "cruz";
    }

    @FXML
    public void seleccionarEditarColor(ActionEvent e) {
        resetModes();
        markingEnabled = true;
        currentTool = Tool.EDIT_COLOR;
        colorPickerEdicion.show();
    }

    @FXML
    private void about(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Acerca de");
        alert.setHeaderText("IPC - 2025");
        alert.setWidth(600);
        alert.setHeight(600);
        alert.setContentText(
                "Al iniciar un elemento click izquierdo para añadir ese elemento y click derecho para cancelar.\n" +
                        "Si no se realiza un problema y se cierra sesión las estadísticas no se actualizarán.");
        alert.show();
    }

    @FXML
    public void zoomIn(ActionEvent e) {
        zoom_slider.setValue(zoom_slider.getValue() + 0.1);
    }

    @FXML
    public void zoomOut(ActionEvent e) {
        zoom_slider.setValue(zoom_slider.getValue() - 0.1);
    }

    @FXML
    public void seleccionarArcoRadioFijo(ActionEvent e) {
        modoArco = ModoArco.RADIO_FIJO;
        seleccionarTrazarArco(e);
    }

    @FXML
    public void seleccionarArcoRadioLibre(ActionEvent e) {
        modoArco = ModoArco.RADIO_LIBRE;
        seleccionarTrazarArco(e);
    }
}
