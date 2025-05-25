package View;

import Methods.Models.Tower;
import Methods.Models.Discs;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import java.util.List;

/**
 * Clase que construye y maneja la interfaz gráfica del juego
 * Contiene todos los elementos visuales y controles de la aplicación
 */
public class ScreenView {

    // Componentes principales
    private Stage primaryStage;
    private Scene scene;
    private BorderPane root;
    private Pane gameArea;

    // Controles de la interfaz
    private ComboBox<Integer> discSelector;
    private Button startButton;
    private Button resetButton;
    private Button saveHistoryButton;
    private TextArea historyArea;
    private Label moveCountLabel;
    private Label gameInfoLabel;
    private ProgressBar progressBar;

    // Elementos visuales del juego
    private Rectangle[] towerBases;
    private Line[] towerPoles;
    private Label[] towerLabels;

    // Dimensiones y configuración
    private static final double WINDOW_WIDTH = 1000.0;
    private static final double WINDOW_HEIGHT = 700.0;
    private static final double GAME_AREA_HEIGHT = 400.0;
    private static final double TOWER_BASE_WIDTH = 200.0;
    private static final double TOWER_BASE_HEIGHT = 20.0;
    private static final double TOWER_POLE_HEIGHT = 300.0;
    private static final double TOWER_SPACING = 250.0;

    /**
     * Constructor de la pantalla principal
     * @param primaryStage Escenario principal de JavaFX
     */
    public ScreenView(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Inicializar componentes y layout
        initializeComponents();
        setupLayout();
        setupStyling();
        createScene();
    }

    /**
     * Inicializa todos los componentes de la interfaz
     */
    private void initializeComponents() {
        // Crear el layout principal
        root = new BorderPane();

        // Crear el área de juego
        gameArea = new Pane();
        gameArea.setPrefHeight(GAME_AREA_HEIGHT);

        // Inicializar controles
        discSelector = new ComboBox<>();
        discSelector.getItems().addAll(3, 4, 5, 6);
        discSelector.setValue(3);
        discSelector.setPromptText("Número de discos");

        startButton = new Button("Iniciar Simulación");
        resetButton = new Button("Reiniciar Juego");
        saveHistoryButton = new Button("Guardar Historial");

        historyArea = new TextArea();
        historyArea.setEditable(false);
        historyArea.setPrefRowCount(15);

        moveCountLabel = new Label("Movimientos: 0");
        gameInfoLabel = new Label("Torres de Hanoi");
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);

        // Inicializar elementos visuales del juego
        initializeGameVisuals();
    }

    /**
     * Inicializa los elementos visuales del juego (torres, bases, etiquetas)
     */
    private void initializeGameVisuals() {
        towerBases = new Rectangle[3];
        towerPoles = new Line[3];
        towerLabels = new Label[3];

        // Calcular la posición central para las torres
        double centerX = WINDOW_WIDTH / 2;
        double firstTowerX = centerX - TOWER_SPACING;

        for (int i = 0; i < 3; i++) {
            // Calcular posición X de cada torre (centrada en la ventana)
            double x = firstTowerX + (i * TOWER_SPACING) - (TOWER_BASE_WIDTH / 2);
            double y = GAME_AREA_HEIGHT - TOWER_BASE_HEIGHT;

            // Crear base de la torre
            towerBases[i] = new Rectangle(x, y, TOWER_BASE_WIDTH, TOWER_BASE_HEIGHT);
            towerBases[i].setFill(Color.DARKGRAY);
            towerBases[i].setStroke(Color.BLACK);
            towerBases[i].setStrokeWidth(2);
            towerBases[i].setArcWidth(15);
            towerBases[i].setArcHeight(15);

            // Crear poste de la torre
            double poleX = x + (TOWER_BASE_WIDTH / 2);
            double poleStartY = y;
            double poleEndY = poleStartY - TOWER_POLE_HEIGHT;

            towerPoles[i] = new Line(poleX, poleStartY, poleX, poleEndY);
            towerPoles[i].setStrokeWidth(8);
            towerPoles[i].setStroke(Color.BROWN);

            // Crear etiqueta de la torre
            towerLabels[i] = new Label(String.valueOf((char)('A' + i)));
            towerLabels[i].setLayoutX(x + (TOWER_BASE_WIDTH / 2) - 5);
            towerLabels[i].setLayoutY(y + 25);
            towerLabels[i].setFont(Font.font("Arial", FontWeight.BOLD, 20));
            towerLabels[i].setTextFill(Color.WHITE);

            // Agregar elementos al área de juego (primero los postes, luego las bases)
            gameArea.getChildren().add(towerPoles[i]);
            gameArea.getChildren().add(towerBases[i]);
            gameArea.getChildren().add(towerLabels[i]);
        }
    }

    /**
     * Configura el layout principal de la aplicación
     */
    private void setupLayout() {
        // Panel superior con controles
        HBox topPanel = createTopControlsPanel();
        root.setTop(topPanel);

        // Panel central con área de juego
        root.setCenter(gameArea);

        // Panel izquierdo con información
        VBox leftPanel = createLeftPanel();
        root.setLeft(leftPanel);

        // Panel derecho con historial
        VBox rightPanel = createRightPanel();
        root.setRight(rightPanel);
    }

    /**
     * Crea el panel superior con controles principales
     * @return HBox con los controles
     */
    private HBox createTopControlsPanel() {
        HBox panel = new HBox(15);
        panel.setPadding(new Insets(15));
        panel.setAlignment(Pos.CENTER);

        Label selectorLabel = new Label("Discos:");
        selectorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        panel.getChildren().addAll(
                selectorLabel, discSelector,
                startButton,
                resetButton,
                saveHistoryButton
        );

        return panel;
    }

    /**
     * Crea el panel izquierdo con información del juego
     * @return VBox con información
     */
    private VBox createLeftPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(200);
        panel.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Información");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        VBox.setMargin(progressBar, new Insets(10, 0, 0, 0));

        panel.getChildren().addAll(
                titleLabel,
                gameInfoLabel,
                moveCountLabel,
                new Label("Progreso:"),
                progressBar
        );

        return panel;
    }

    /**
     * Crea el panel derecho con el historial de movimientos
     * @return VBox con historial
     */
    private VBox createRightPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setPrefWidth(300);

        Label titleLabel = new Label("Historial de Movimientos");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        ScrollPane scrollPane = new ScrollPane(historyArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        panel.getChildren().addAll(titleLabel, scrollPane);

        return panel;
    }

    /**
     * Configura los estilos de los componentes
     */
    private void setupStyling() {
        // Aplicar estilos a los botones
        String buttonStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;";
        startButton.setStyle(buttonStyle);

        resetButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        saveHistoryButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        // Estilo para el área de juego
        gameArea.setStyle("-fx-background-color: #333333; -fx-border-color: #555555; -fx-border-width: 2px;");

        // Estilo para el historial
        historyArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        // Estilo para las etiquetas
        gameInfoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        moveCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");

        // Estilo para el selector de discos
        discSelector.setStyle("-fx-font-size: 14px;");
    }

    /**
     * Crea la escena principal
     */
    private void createScene() {
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Aplicar estilos generales a la escena
        scene.getRoot().setStyle(
                "-fx-font-family: 'Arial';" +
                        "-fx-background-color: #f5f5f5;"
        );
    }

    /**
     * Dibuja el estado inicial del juego con las torres y discos
     * @param towers Array de torres del juego
     */
    public void drawInitialState(Tower[] towers) {
        if (towers == null || towers.length != 3) {
            return;
        }

        // Limpiar discos existentes
        gameArea.getChildren().removeIf(node ->
                node instanceof Rectangle &&
                        !java.util.Arrays.asList(towerBases).contains(node)
        );

        // Dibujar discos en sus posiciones iniciales
        for (Tower tower : towers) {
            List<Discs> discs = tower.getAllDiscs();
            for (Discs disc : discs) {
                Rectangle visual = disc.getVisual();
                if (!gameArea.getChildren().contains(visual)) {
                    // Asegurar que el disco aparezca por debajo de las etiquetas de torre
                    gameArea.getChildren().add(gameArea.getChildren().size() - 3, visual);
                }
            }
        }
    }

    /**
     * Actualiza las posiciones de las torres en HanoiGame para que coincidan con la vista
     * @param towers Array de torres del juego
     */
    public void updateTowerPositions(Tower[] towers) {
        if (towers == null || towers.length != 3 || towerBases == null) {
            return;
        }

        for (int i = 0; i < 3; i++) {
            double x = towerBases[i].getX();
            double y = towerBases[i].getY();

            // Actualizar posición en el modelo
            towers[i].setPosition(x, y);
        }
    }

    /**
     * Actualiza el contador de movimientos
     * @param count Número actual de movimientos
     */
    public void updateMoveCount(int count) {
        moveCountLabel.setText("Movimientos: " + count);
    }

    /**
     * Actualiza el área de historial
     * @param text Texto a agregar
     */
    public void updateHistoryArea(String text) {
        historyArea.appendText(text + "\n");
        historyArea.positionCaret(historyArea.getText().length());
    }

    /**
     * Actualiza la información del juego
     * @param info Información a mostrar
     */
    public void updateGameInfo(String info) {
        gameInfoLabel.setText(info);
    }

    /**
     * Actualiza la barra de progreso
     * @param progress Progreso entre 0.0 y 1.0
     */
    public void updateProgress(double progress) {
        progressBar.setProgress(progress);
    }

    /**
     * Habilita o deshabilita el botón de inicio
     * @param enabled true para habilitar
     */
    public void enableStartButton(boolean enabled) {
        startButton.setDisable(!enabled);
    }

    /**
     * Habilita o deshabilita el botón de reset
     * @param enabled true para habilitar
     */
    public void enableResetButton(boolean enabled) {
        resetButton.setDisable(!enabled);
    }

    /**
     * Habilita o deshabilita el botón de guardar historial
     * @param enabled true para habilitar
     */
    public void enableSaveHistoryButton(boolean enabled) {
        saveHistoryButton.setDisable(!enabled);
    }

    /**
     * Muestra un mensaje de error
     * @param message Mensaje de error
     */
    public void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje informativo
     * @param message Mensaje informativo
     */
    public void showInfoMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Limpia el historial de movimientos
     */
    public void clearHistory() {
        historyArea.clear();
    }

    // Getters para acceder a los componentes desde el controlador
    public ComboBox<Integer> getDiscSelector() {
        return discSelector;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Button getSaveHistoryButton() {
        return saveHistoryButton;
    }

    public TextArea getHistoryArea() {
        return historyArea;
    }

    public Pane getGameArea() {
        return gameArea;
    }

    public Scene getScene() {
        return scene;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Configura el título de la ventana y muestra la aplicación
     */
    public void show() {
        primaryStage.setTitle("Torres de Hanoi - Simulador");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }
}