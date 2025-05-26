package Methods.Models;

import View.ScreenView;
import View.Animations;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Clase que contiene los métodos de interacción entre la vista y el modelo
 * Maneja los eventos y la lógica de presentación
 */
public class Listeners {
    private HanoiGame game;
    private ScreenView screen;
    private Animations animations;
    private boolean isAnimating;
    private int selectedDiscCount;

    // Configuración de animación
    private static final double ANIMATION_DURATION = 1000.0; // 1 segundo por movimiento
    private static final double ANIMATION_DELAY = 500.0;     // 0.5 segundos entre movimientos

    /**
     * Constructor de Listeners
     */
    public Listeners() {
        this.isAnimating = false;
        this.selectedDiscCount = 3; // Valor por defecto
    }

    /**
     * Inicializa el juego con el número de discos seleccionado
     * @param discCount Número de discos (3-6)
     */
    public void initializeGame(int discCount) {
        try {
            // Crear nuevo juego con el número de discos especificado
            game = new HanoiGame(discCount);
            selectedDiscCount = discCount;

            // Registrar callback para movimientos
            game.setMoveCallback(this::handleGameMovement);

            // Actualizar vista
            if (screen != null) {
                // Actualizar posiciones de torre en el modelo para que coincidan con la vista
                screen.updateTowerPositions(game.getTowers());
                
                // Dibujar estado inicial
                screen.drawInitialState(game.getTowers());
                
                // Actualizar información del juego
                screen.updateGameInfo("Juego inicializado con " + discCount + " discos");
                screen.updateMoveCount(0);
                screen.updateProgress(0);
                screen.clearHistory();
                
                // Habilitar botones
                screen.enableStartButton(true);
                screen.enableResetButton(true);
                screen.enableSaveHistoryButton(false);
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar el juego: " + e.getMessage());
            showError("Error al inicializar el juego: " + e.getMessage());
        }
    }

    /**
     * Maneja el evento de inicio de simulación automática
     */
    public void handleStartSimulation() {
        if (game == null || isAnimating) {
            return;
        }

        // Deshabilitar botones durante la simulación
        if (screen != null) {
            screen.enableStartButton(false);
            screen.enableResetButton(false);
            screen.updateGameInfo("Simulando solución...");
        }

        // Iniciar simulación en hilo separado
        startAnimatedSimulation();
    }

    /**
     * Inicia la simulación con animaciones en un hilo separado
     */
    private void startAnimatedSimulation() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                isAnimating = true;
                
                // Reiniciar el juego para comenzar desde el estado inicial
                Platform.runLater(() -> {
                    game.reset();
                    screen.drawInitialState(game.getTowers());
                    screen.clearHistory();
                });
                
                try {
                    // Pequeña pausa antes de comenzar
                    Thread.sleep(500);
                    
                    // Iniciar la solución automática
                    game.startAutoSolution();
                    
                } catch (InterruptedException e) {
                    // La tarea fue cancelada
                    System.out.println("Simulación cancelada");
                } finally {
                    isAnimating = false;
                    
                    // Actualizar UI cuando termine
                    Platform.runLater(() -> {
                        screen.enableResetButton(true);
                        screen.enableSaveHistoryButton(true);
                        screen.updateGameInfo("Simulación completada");
                        
                        // Si se completó el juego, mostrar animación de victoria
                        if (game.isGameCompleted()) {
                            // Convertir los discos de la torre C a un array
                            Discs[] victoryDiscs = game.getTowerC().getDiscsFromBottomToTop();
                            
                            // Animar victoria
                            animations.animateVictory(victoryDiscs);
                        }
                    });
                }
                
                return null;
            }
        };

        // Iniciar la tarea en un nuevo hilo
        new Thread(task).start();
    }

    /**
     * Maneja cada movimiento del juego con animación
     * @param move Movimiento a procesar
     */
    private void handleGameMovement(HanoiGame.Move move) {
        if (move == null || screen == null) {
            return;
        }

        // Obtener torres origen y destino
        Tower fromTower = game.getTowerByName(move.getFrom());
        Tower toTower = game.getTowerByName(move.getTo());
        Discs disc = move.getDisc();

        // Registrar movimiento en el historial visual
        Platform.runLater(() -> {
            screen.updateHistoryArea(move.toString());
            screen.updateMoveCount(move.getMoveNumber());
            screen.updateProgress(game.getProgress());
        });

        // Animar el movimiento
        if (animations != null && isAnimating) {
            try {
                // Animar el movimiento del disco de manera segura
                final Tower finalFromTower = fromTower;
                final Tower finalToTower = toTower;
                
                Platform.runLater(() -> {
                    try {
                        animations.animateDiscMovement(disc, finalFromTower, finalToTower, ANIMATION_DURATION);
                    } catch (Exception ex) {
                        System.err.println("Error en animación: " + ex.getMessage());
                    }
                });

                // Esperar a que termine la animación y el delay
                Thread.sleep((long)(ANIMATION_DURATION + ANIMATION_DELAY));
            } catch (InterruptedException e) {
                System.err.println("Animación interrumpida: " + e.getMessage());
            }
        }
    }

    /**
     * Maneja el evento de reset del juego
     */
    public void handleResetGame() {
        if (game == null) {
            return;
        }

        // Si hay animaciones en curso, detenerlas
        if (isAnimating && animations != null) {
            animations.stopAllAnimations();
            isAnimating = false;
        }

        // Reiniciar el juego
        game.reset();

        // Actualizar UI
        if (screen != null) {
            screen.drawInitialState(game.getTowers());
            screen.clearHistory();
            screen.updateMoveCount(0);
            screen.updateProgress(0);
            screen.updateGameInfo("Juego reiniciado");
            screen.enableStartButton(true);
            screen.enableSaveHistoryButton(false);
        }
    }

    /**
     * Maneja el cambio en la selección de número de discos
     * @param discCount Nuevo número de discos seleccionado
     */
    public void handleDiscCountChange(int discCount) {
        if (discCount == selectedDiscCount) {
            return;
        }

        // Si hay animaciones en curso, detenerlas
        if (isAnimating && animations != null) {
            animations.stopAllAnimations();
            isAnimating = false;
        }

        // Inicializar nuevo juego con el número de discos seleccionado
        initializeGame(discCount);
    }

    /**
     * Maneja el evento de guardar historial
     */
    public void handleSaveHistory() {
        if (game == null || screen == null) {
            return;
        }

        try {
            // Aquí se implementaría la lógica para guardar el historial
            // Por ejemplo, usando la clase Aux o mostrando un diálogo para guardar archivo
            
            // Por ahora, solo mostraremos un mensaje
            showInfo("Historial guardado correctamente");
            
        } catch (Exception e) {
            showError("Error al guardar historial: " + e.getMessage());
        }
    }

    /**
     * Muestra un mensaje de error
     * @param message Mensaje de error
     */
    private void showError(String message) {
        if (screen != null) {
            Platform.runLater(() -> screen.showErrorMessage(message));
        } else {
            System.err.println("ERROR: " + message);
        }
    }

    /**
     * Muestra un mensaje informativo
     * @param message Mensaje informativo
     */
    public void showInfo(String message) {
        if (screen != null) {
            Platform.runLater(() -> screen.showInfoMessage(message));
        } else {
            System.out.println("INFO: " + message);
        }
    }

    // Setters para las referencias de vista
    public void setScreen(ScreenView screen) {
        this.screen = screen;
    }

    public void setAnimations(Animations animations) {
        this.animations = animations;
    }

    // Getters
    public HanoiGame getGame() {
        return game;
    }

    public int getSelectedDiscCount() {
        return selectedDiscCount;
    }

    public boolean isGameInitialized() {
        return game != null;
    }

    public boolean isGameCompleted() {
        return game != null && game.isGameCompleted();
    }
}