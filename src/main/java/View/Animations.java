package View;

import Methods.Models.Discs;
import Methods.Models.Tower;
import javafx.animation.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que maneja todas las animaciones del juego
 * Incluye movimientos verticales, horizontales y combinados para simular movimiento real
 */
public class Animations {

    // Configuración de animación
    private static final double LIFT_HEIGHT = 100.0;  // Altura a la que se eleva el disco
    private static final Duration DEFAULT_DURATION = Duration.millis(800);
    private static final Duration LIFT_DURATION = Duration.millis(300);
    private static final Duration MOVE_DURATION = Duration.millis(500);
    private static final Duration DROP_DURATION = Duration.millis(300);

    private boolean animationInProgress;
    private Map<Rectangle, SequentialTransition> activeAnimations;

    /**
     * Constructor de la clase Animations
     */
    public Animations() {
        this.animationInProgress = false;
        this.activeAnimations = new HashMap<>();
    }

    /**
     * Anima el movimiento completo de un disco de una torre a otra
     * @param disc Disco a mover
     * @param fromTower Torre origen
     * @param toTower Torre destino
     * @param duration Duración total de la animación
     */
    public void animateDiscMovement(Discs disc, Tower fromTower, Tower toTower, double duration) {
        if (disc == null || fromTower == null || toTower == null) {
            return;
        }

        animationInProgress = true;
        Rectangle visual = disc.getVisual();

        // Si ya hay una animación activa para este disco, la dejamos terminar
        if (activeAnimations.containsKey(visual)) {
            // No intentamos detenerla, simplemente no creamos una nueva
            return;
        }

        // Posiciones actuales
        double startX = visual.getX();
        double startY = visual.getY();

        // Posiciones finales
        double endX = toTower.getX() + (toTower.getBaseWidth() - disc.getWidth()) / 2;
        double endY = toTower.getY() - ((toTower.getDiscCount()) * disc.getHeight());

        // Crear secuencia de animaciones
        SequentialTransition sequence = createMovementSequence(visual, startX, startY, endX, endY);

        // Guardar la animación en el mapa de animaciones activas
        activeAnimations.put(visual, sequence);

        sequence.setOnFinished(e -> {
            // Eliminar del mapa de animaciones activas cuando termina
            activeAnimations.remove(visual);
            
            // Actualizar estado solo si no hay más animaciones activas
            if (activeAnimations.isEmpty()) {
                animationInProgress = false;
            }
            
            // Actualizar posición final del disco
            disc.setPosition(endX, endY);
        });

        sequence.play();
    }

    /**
     * Crea la secuencia completa de movimiento (subir, mover, bajar)
     * @param visual Elemento visual a animar
     * @param startX Posición X inicial
     * @param startY Posición Y inicial
     * @param endX Posición X final
     * @param endY Posición Y final
     * @return SequentialTransition con todas las animaciones
     */
    private SequentialTransition createMovementSequence(Rectangle visual,
                                                        double startX, double startY,
                                                        double endX, double endY) {
        SequentialTransition sequence = new SequentialTransition();

        // 1. Animación de elevación (subir)
        Timeline liftAnimation = createLiftAnimation(visual, startY);

        // 2. Animación horizontal (mover de lado)
        Timeline horizontalAnimation = createHorizontalAnimation(visual, startX, endX);

        // 3. Animación de descenso (bajar)
        Timeline dropAnimation = createDropAnimation(visual, endY);

        sequence.getChildren().addAll(liftAnimation, horizontalAnimation, dropAnimation);

        return sequence;
    }

    /**
     * Crea la animación de elevación del disco
     * @param visual Elemento a animar
     * @param startY Posición Y inicial
     * @return Timeline de elevación
     */
    private Timeline createLiftAnimation(Rectangle visual, double startY) {
        Timeline liftTimeline = new Timeline();

        // Calcular altura de elevación relativa
        double targetY = startY - LIFT_HEIGHT;

        KeyValue keyValue = new KeyValue(visual.yProperty(), targetY, Interpolator.EASE_OUT);
        KeyFrame keyFrame = new KeyFrame(LIFT_DURATION, keyValue);

        liftTimeline.getKeyFrames().add(keyFrame);

        return liftTimeline;
    }

    /**
     * Crea la animación horizontal del disco
     * @param visual Elemento a animar
     * @param startX Posición X inicial
     * @param endX Posición X final
     * @return Timeline de movimiento horizontal
     */
    private Timeline createHorizontalAnimation(Rectangle visual, double startX, double endX) {
        Timeline horizontalTimeline = new Timeline();

        KeyValue keyValue = new KeyValue(visual.xProperty(), endX, Interpolator.EASE_BOTH);
        KeyFrame keyFrame = new KeyFrame(MOVE_DURATION, keyValue);

        horizontalTimeline.getKeyFrames().add(keyFrame);

        return horizontalTimeline;
    }

    /**
     * Crea la animación de descenso del disco
     * @param visual Elemento a animar
     * @param endY Posición Y final
     * @return Timeline de descenso
     */
    private Timeline createDropAnimation(Rectangle visual, double endY) {
        Timeline dropTimeline = new Timeline();

        KeyValue keyValue = new KeyValue(visual.yProperty(), endY, Interpolator.EASE_IN);
        KeyFrame keyFrame = new KeyFrame(DROP_DURATION, keyValue);

        dropTimeline.getKeyFrames().add(keyFrame);

        return dropTimeline;
    }

    /**
     * Añade un efecto visual cuando el disco se eleva
     * @param visual Elemento visual
     */
    private void addElevationEffect(Rectangle visual) {
        // Efecto de brillo temporal
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), visual);
        scaleUp.setToX(1.05);
        scaleUp.setToY(1.05);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), visual);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        SequentialTransition blink = new SequentialTransition(scaleUp, scaleDown);
        blink.play();
    }

    /**
     * Añade un efecto visual cuando el disco aterriza
     * @param visual Elemento visual
     */
    private void addLandingEffect(Rectangle visual) {
        // Efecto de rebote sutil
        TranslateTransition bounce = new TranslateTransition(Duration.millis(100), visual);
        bounce.setByY(3);
        bounce.setCycleCount(2);
        bounce.setAutoReverse(true);
        bounce.play();
    }

    /**
     * Anima la aparición inicial de los discos
     * @param disc Disco a animar
     * @param delay Retraso antes de iniciar la animación
     */
    public void animateDiscAppearance(Discs disc, double delay) {
        Rectangle visual = disc.getVisual();

        // Iniciar invisible y pequeño
        visual.setOpacity(0.0);
        visual.setScaleX(0.1);
        visual.setScaleY(0.1);

        // Animación de aparición
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), visual);
        fadeIn.setToValue(1.0);

        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), visual);
        scaleIn.setToX(1.0);
        scaleIn.setToY(1.0);

        ParallelTransition appearance = new ParallelTransition(fadeIn, scaleIn);
        appearance.setDelay(Duration.millis(delay));
        appearance.play();
    }

    /**
     * Anima la desaparición de todos los discos (para reset)
     * @param discs Array de discos a animar
     * @param onFinished Callback al terminar
     */
    public void animateDiscDisappearance(Discs[] discs, Runnable onFinished) {
        if (discs == null || discs.length == 0) {
            if (onFinished != null) onFinished.run();
            return;
        }

        ParallelTransition disappearance = new ParallelTransition();

        for (int i = 0; i < discs.length; i++) {
            Rectangle visual = discs[i].getVisual();

            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), visual);
            fadeOut.setToValue(0.0);
            fadeOut.setDelay(Duration.millis(i * 50)); // Efecto escalonado

            disappearance.getChildren().add(fadeOut);
        }

        if (onFinished != null) {
            disappearance.setOnFinished(e -> onFinished.run());
        }

        disappearance.play();
    }

    /**
     * Crea una animación de celebración cuando se completa el juego
     * @param discs Discos de la torre ganadora
     */
    public void animateVictory(Discs[] discs) {
        if (discs == null || discs.length == 0) return;

        for (int i = 0; i < discs.length; i++) {
            Rectangle visual = discs[i].getVisual();

            // Animación de celebración escalonada
            Timeline celebration = new Timeline();

            // Movimiento de rebote
            KeyFrame bounce1 = new KeyFrame(Duration.millis(200),
                    new KeyValue(visual.translateYProperty(), -15, Interpolator.EASE_OUT));
            KeyFrame bounce2 = new KeyFrame(Duration.millis(400),
                    new KeyValue(visual.translateYProperty(), 0, Interpolator.EASE_OUT));

            celebration.getKeyFrames().addAll(bounce1, bounce2);
            celebration.setDelay(Duration.millis(i * 100));
            celebration.setCycleCount(3);
            celebration.play();
        }
    }

    /**
     * Detiene todas las animaciones
     */
    public void stopAllAnimations() {
        // Detener todas las animaciones activas de manera segura
        for (SequentialTransition animation : activeAnimations.values()) {
            // Sólo detenemos si no está en una transición mayor
            if (animation.getStatus() == Animation.Status.RUNNING) {
                try {
                    animation.stop();
                } catch (IllegalStateException e) {
                    // Ignoramos este error específico que ocurre al intentar detener
                    // una animación que es parte de otra
                    System.out.println("No se pudo detener una animación: " + e.getMessage());
                }
            }
        }
        
        // Limpiar colección
        activeAnimations.clear();
        animationInProgress = false;
    }

    // Getters
    public boolean isAnimationInProgress() {
        return animationInProgress;
    }

    /**
     * Establece la duración base de las animaciones
     * @param duration Nueva duración en milisegundos
     */
    public void setAnimationDuration(double duration) {
        // Implementación si se requiere configuración dinámica
    }
}