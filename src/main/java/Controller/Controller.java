package Controller;

import Methods.Models.Listeners;
import View.Animations;
import View.ScreenView;
import javafx.stage.Stage;

/**
 * Controlador principal que orquesta la interacción entre modelo y vista
 * Siguiendo el patrón MVC, solo llama a los métodos de Listeners
 */
public class Controller {
    private Listeners listeners;
    private ScreenView screen;
    private Animations animations;

    /**
     * Constructor del controlador
     * @param primaryStage Escenario principal de JavaFX
     */
    public Controller(Stage primaryStage) {
        // Inicializar componentes
        initializeComponents(primaryStage);

        // Configurar eventos
        setupEventHandlers();

        // Mostrar la aplicación
        screen.show();

        // Inicializar juego por defecto
        listeners.initializeGame(3);
    }

    /**
     * Inicializa todos los componentes del MVC
     * @param primaryStage Escenario principal
     */
    private void initializeComponents(Stage primaryStage) {
        // Crear vista
        screen = new ScreenView(primaryStage);

        // Crear animaciones
        animations = new Animations();

        // Crear listeners del modelo
        listeners = new Listeners();

        // Establecer referencias cruzadas
        listeners.setScreen(screen);
        listeners.setAnimations(animations);
    }

    /**
     * Configura todos los manejadores de eventos
     */
    private void setupEventHandlers() {
        // Evento del botón Iniciar
        screen.getStartButton().setOnAction(e -> handleStartSimulation());

        // Evento del botón Reset
        screen.getResetButton().setOnAction(e -> handleResetGame());

        // Evento del botón Guardar Historial
        screen.getSaveHistoryButton().setOnAction(e -> handleSaveHistory());

        // Evento del selector de discos
        screen.getDiscSelector().setOnAction(e -> handleDiscCountChange());

        // Evento de cierre de ventana
        screen.getPrimaryStage().setOnCloseRequest(e -> handleApplicationExit());
    }

    /**
     * Maneja el evento de inicio de simulación
     */
    private void handleStartSimulation() {
        listeners.handleStartSimulation();
    }

    /**
     * Maneja el evento de reset del juego
     */
    private void handleResetGame() {
        listeners.handleResetGame();
    }

    /**
     * Maneja el evento de guardar historial
     */
    private void handleSaveHistory() {
        listeners.handleSaveHistory();
    }

    /**
     * Maneja el cambio en la selección de número de discos
     */
    private void handleDiscCountChange() {
        Integer selectedDiscs = screen.getDiscSelector().getValue();
        if (selectedDiscs != null) {
            listeners.handleDiscCountChange(selectedDiscs);
        }
    }

    /**
     * Maneja el cierre de la aplicación
     */
    private void handleApplicationExit() {
        // Detener animaciones si están en curso
        if (animations.isAnimationInProgress()) {
            animations.stopAllAnimations();
        }

        // Realizar limpieza si es necesaria
        System.out.println("Cerrando aplicación Torres de Hanoi...");
    }

    // Getters para acceso a componentes (si es necesario)
    public Listeners getListeners() {
        return listeners;
    }

    public ScreenView getScreen() {
        return screen;
    }

    public Animations getAnimations() {
        return animations;
    }
}
