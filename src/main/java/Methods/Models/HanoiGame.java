package Methods.Models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Clase principal que controla la lógica del juego Torres de Hanoi
 * Maneja las torres, discos y el algoritmo recursivo de resolución
 */
public class HanoiGame {
    private Tower[] towers;                    // Array de torres [A, B, C]
    private int numberOfDiscs;                 // Número de discos en el juego
    private List<String> moveHistory;          // Historial de movimientos
    private int moveCount;                     // Contador de movimientos
    private boolean gameCompleted;             // Estado del juego
    private boolean gameInProgress;            // Si hay una simulación en curso

    // Callback para notificar movimientos a la vista
    private Consumer<Move> moveCallback;

    // Posiciones de las torres en pantalla
    private static final double TOWER_SPACING = 250.0;
    private static final double FIRST_TOWER_X = 100.0;
    private static final double TOWER_Y = 350.0;

    /**
     * Clase interna para representar un movimiento
     */
    public static class Move {
        private final String from;
        private final String to;
        private final Discs disc;
        private final int moveNumber;

        public Move(String from, String to, Discs disc, int moveNumber) {
            this.from = from;
            this.to = to;
            this.disc = disc;
            this.moveNumber = moveNumber;
        }

        // Getters
        public String getFrom() { return from; }
        public String getTo() { return to; }
        public Discs getDisc() { return disc; }
        public int getMoveNumber() { return moveNumber; }

        @Override
        public String toString() {
            return String.format("Movimiento %d: Disco %d de Torre %s a Torre %s",
                    moveNumber, disc.getSize(), from, to);
        }
    }

    /**
     * Constructor del juego
     * @param numberOfDiscs Número de discos (3-6)
     */
    public HanoiGame(int numberOfDiscs) {
        if (numberOfDiscs < 3 || numberOfDiscs > 6) {
            throw new IllegalArgumentException("El número de discos debe estar entre 3 y 6");
        }

        this.numberOfDiscs = numberOfDiscs;
        this.moveHistory = new ArrayList<>();
        this.moveCount = 0;
        this.gameCompleted = false;
        this.gameInProgress = false;

        initializeTowers();
        initializeDiscs();
    }

    /**
     * Inicializa las tres torres en sus posiciones
     */
    private void initializeTowers() {
        towers = new Tower[3];
        towers[0] = new Tower("A", FIRST_TOWER_X, TOWER_Y, numberOfDiscs);
        towers[1] = new Tower("B", FIRST_TOWER_X + TOWER_SPACING, TOWER_Y, numberOfDiscs);
        towers[2] = new Tower("C", FIRST_TOWER_X + (TOWER_SPACING * 2), TOWER_Y, numberOfDiscs);
    }

    /**
     * Inicializa los discos en la torre A (del más grande al más pequeño)
     */
    private void initializeDiscs() {
        // Crear discos del más grande al más pequeño y colocarlos en torre A
        for (int i = numberOfDiscs; i >= 1; i--) {
            Discs disc = new Discs(i);
            towers[0].pushDisc(disc);
        }
    }

    /**
     * Reinicia el juego a su estado inicial
     */
    public void reset() {
        // Limpiar torres
        for (Tower tower : towers) {
            tower.clear();
        }

        // Reinicializar discos
        initializeDiscs();

        // Limpiar historial
        moveHistory.clear();
        moveCount = 0;
        gameCompleted = false;
        gameInProgress = false;
    }

    /**
     * Establece el callback para notificar movimientos
     * @param callback Función que será llamada en cada movimiento
     */
    public void setMoveCallback(Consumer<Move> callback) {
        this.moveCallback = callback;
    }

    /**
     * Inicia la resolución automática del juego
     */
    public void startAutoSolution() {
        if (gameInProgress) {
            return; // Ya hay una simulación en curso
        }

        gameInProgress = true;
        gameCompleted = false;

        // Resolver usando algoritmo recursivo
        solveHanoi(numberOfDiscs, towers[0], towers[2], towers[1]);

        gameCompleted = true;
        gameInProgress = false;
    }

    /**
     * Algoritmo recursivo para resolver las Torres de Hanoi
     * @param n Número de discos a mover
     * @param source Torre origen
     * @param destination Torre destino
     * @param auxiliary Torre auxiliar
     */
    private void solveHanoi(int n, Tower source, Tower destination, Tower auxiliary) {
        if (n == 1) {
            // Caso base: mover un solo disco
            moveDisc(source, destination);
        } else {
            // Paso 1: Mover n-1 discos de origen a auxiliar
            solveHanoi(n - 1, source, auxiliary, destination);

            // Paso 2: Mover el disco más grande a destino
            moveDisc(source, destination);

            // Paso 3: Mover n-1 discos de auxiliar a destino
            solveHanoi(n - 1, auxiliary, destination, source);
        }
    }

    /**
     * Mueve un disco de una torre a otra
     * @param from Torre origen
     * @param to Torre destino
     * @return true si el movimiento fue exitoso
     */
    public boolean moveDisc(Tower from, Tower to) {
        if (from == null || to == null) {
            return false;
        }

        // Verificar si el movimiento es válido
        if (!from.canMoveTo(to)) {
            return false;
        }

        // Realizar el movimiento
        Discs disc = from.popDisc();
        if (disc != null && to.pushDisc(disc)) {
            moveCount++;

            // Crear registro del movimiento
            Move move = new Move(from.getName(), to.getName(), disc, moveCount);
            moveHistory.add(move.toString());

            // Notificar a la vista si hay callback
            if (moveCallback != null) {
                moveCallback.accept(move);
            }

            return true;
        }

        // Si falla, devolver el disco a la torre original
        if (disc != null) {
            from.pushDisc(disc);
        }
        return false;
    }

    /**
     * Mueve un disco especificando las torres por nombre
     * @param fromName Nombre de torre origen ("A", "B", "C")
     * @param toName Nombre de torre destino ("A", "B", "C")
     * @return true si el movimiento fue exitoso
     */
    public boolean moveDisc(String fromName, String toName) {
        Tower from = getTowerByName(fromName);
        Tower to = getTowerByName(toName);

        if (from == null || to == null) {
            return false;
        }

        return moveDisc(from, to);
    }

    /**
     * Obtiene una torre por su nombre
     * @param name Nombre de la torre ("A", "B", "C")
     * @return Torre correspondiente o null
     */
    public Tower getTowerByName(String name) {
        for (Tower tower : towers) {
            if (tower.getName().equals(name)) {
                return tower;
            }
        }
        return null;
    }

    /**
     * Verifica si el juego está completado
     * @return true si todos los discos están en la torre C
     */
    public boolean isGameCompleted() {
        Tower towerC = towers[2];
        return towerC.getDiscCount() == numberOfDiscs &&
                towers[0].isEmpty() && towers[1].isEmpty();
    }

    /**
     * Calcula el número mínimo de movimientos para resolver el juego
     * @return Número mínimo de movimientos (2^n - 1)
     */
    public int getMinimumMoves() {
        return (int) Math.pow(2, numberOfDiscs) - 1;
    }

    /**
     * Obtiene el progreso del juego como porcentaje
     * @return Progreso entre 0.0 y 1.0
     */
    public double getProgress() {
        if (numberOfDiscs == 0) return 1.0;
        return (double) towers[2].getDiscCount() / numberOfDiscs;
    }

    /**
     * Obtiene información detallada del estado actual
     * @return String con información completa
     */
    public String getGameState() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Estado del Juego ===\n");
        sb.append("Discos: ").append(numberOfDiscs).append("\n");
        sb.append("Movimientos realizados: ").append(moveCount).append("\n");
        sb.append("Movimientos mínimos: ").append(getMinimumMoves()).append("\n");
        sb.append("Progreso: ").append(String.format("%.1f", getProgress() * 100)).append("%\n");
        sb.append("Completado: ").append(isGameCompleted() ? "Sí" : "No").append("\n\n");

        for (Tower tower : towers) {
            sb.append(tower.getDetailedInfo()).append("\n");
        }

        return sb.toString();
    }

    // Getters
    public Tower[] getTowers() {
        return towers;
    }

    public Tower getTowerA() {
        return towers[0];
    }

    public Tower getTowerB() {
        return towers[1];
    }

    public Tower getTowerC() {
        return towers[2];
    }

    public int getNumberOfDiscs() {
        return numberOfDiscs;
    }

    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    /**
     * Obtiene el último movimiento realizado
     * @return String del último movimiento o null si no hay movimientos
     */
    public String getLastMove() {
        if (moveHistory.isEmpty()) {
            return null;
        }
        return moveHistory.get(moveHistory.size() - 1);
    }

    /**
     * Obtiene una representación visual simple del estado actual
     * @return String con representación ASCII de las torres
     */
    public String getVisualRepresentation() {
        StringBuilder sb = new StringBuilder();
        sb.append("Torre A\t\tTorre B\t\tTorre C\n");
        sb.append("------\t\t------\t\t------\n");

        int maxHeight = Math.max(Math.max(towers[0].getDiscCount(),
                        towers[1].getDiscCount()),
                towers[2].getDiscCount());

        for (int level = maxHeight - 1; level >= 0; level--) {
            for (int towerIndex = 0; towerIndex < 3; towerIndex++) {
                List<Discs> discs = towers[towerIndex].getAllDiscs();
                if (level < discs.size()) {
                    sb.append("  ").append(discs.get(level).getSize()).append("\t\t");
                } else {
                    sb.append("  |\t\t");
                }
            }
            sb.append("\n");
        }

        sb.append("=====\t\t=====\t\t=====\n");
        return sb.toString();
    }
}