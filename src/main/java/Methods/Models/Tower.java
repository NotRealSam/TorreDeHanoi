package Methods.Models;

import java.util.Stack;
import java.util.Iterator;

/**
 * Clase que representa una torre en el juego de Torres de Hanoi
 * Implementa correctamente el comportamiento de Stack (LIFO)
 */
public class Tower {
    private Stack<Discs> discs;
    private String name;
    private double x;
    private double y;
    private double baseWidth;
    private double height;
    private int maxCapacity;

    public static final double TOWER_HEIGHT = 300.0;
    public static final double TOWER_BASE_WIDTH = 200.0;

    /**
     * Constructor de la torre
     */
    public Tower(String name, double x, double y, int maxCapacity) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.maxCapacity = maxCapacity;
        this.baseWidth = TOWER_BASE_WIDTH;
        this.height = TOWER_HEIGHT;
        this.discs = new Stack<>();
    }

    /**
     * Intenta colocar un disco en la torre (PUSH operation)
     * @param disc Disco a colocar
     * @return true si se pudo colocar, false si viola las reglas
     */
    public boolean pushDisc(Discs disc) {
        if (disc == null) {
            return false;
        }

        // Verificar capacidad máxima
        if (discs.size() >= maxCapacity) {
            return false;
        }

        // Verificar regla del juego usando solo operaciones Stack
        if (!discs.empty()) {
            Discs topDisc = discs.peek(); // ✅ Operación Stack correcta
            if (!disc.canBePlacedOn(topDisc)) {
                return false;
            }
        }

        // Colocar el disco
        discs.push(disc); // ✅ Operación Stack correcta
        updateDiscPosition(disc);
        return true;
    }

    /**
     * Remueve y retorna el disco superior (POP operation)
     * @return Disco removido, null si la torre está vacía
     */
    public Discs popDisc() {
        if (discs.empty()) { // ✅ Usar empty() en lugar de isEmpty()
            return null;
        }
        return discs.pop(); // ✅ Operación Stack correcta
    }

    /**
     * Obtiene el disco superior sin removerlo (PEEK operation)
     * @return Disco superior, null si está vacía
     */
    public Discs peekDisc() {
        if (discs.empty()) {
            return null;
        }
        return discs.peek(); // ✅ Operación Stack correcta
    }

    /**
     * Actualiza la posición visual del disco según su posición en la torre
     * @param disc Disco a posicionar
     */
    private void updateDiscPosition(Discs disc) {
        if (disc == null) return;

        // Calcular posición X (centrado en la torre)
        double discX = x + (baseWidth - disc.getWidth()) / 2;

        // ✅ Calcular posición Y correctamente
        // El disco recién agregado está en el tope, su posición es size-1 desde la base
        double discY = y - (discs.size() * disc.getHeight());

        disc.setPosition(discX, discY);
    }

    /**
     * ✅ Actualiza las posiciones de todos los discos usando Iterator (Stack-friendly)
     */
    public void updateAllDiscPositions() {
        if (discs.empty()) return;

        // Crear array temporal para recorrer desde abajo hacia arriba
        Discs[] tempArray = new Discs[discs.size()];

        // Vaciar stack temporalmente manteniendo orden
        Stack<Discs> tempStack = new Stack<>();
        while (!discs.empty()) {
            tempStack.push(discs.pop());
        }

        // Restaurar y posicionar
        int position = 1; // Posición desde la base (1 = primera posición)
        while (!tempStack.empty()) {
            Discs disc = tempStack.pop();
            discs.push(disc); // Restaurar al stack original

            // Calcular posición
            double discX = x + (baseWidth - disc.getWidth()) / 2;
            double discY = y - (position * disc.getHeight());
            disc.setPosition(discX, discY);

            position++;
        }
    }

    /**
     * Verifica si la torre está vacía
     */
    public boolean isEmpty() {
        return discs.empty(); // ✅ Usar empty() método de Stack
    }

    /**
     * Obtiene el número de discos en la torre
     */
    public int getDiscCount() {
        return discs.size(); // ✅ size() es válido para Stack
    }

    /**
     * Verifica si la torre está llena
     */
    public boolean isFull() {
        return discs.size() >= maxCapacity;
    }

    /**
     * ✅ Método Stack-friendly para obtener información sin violar encapsulation
     * Retorna array de discos desde la base hasta el tope
     */
    public Discs[] getDiscsFromBottomToTop() {
        if (discs.empty()) {
            return new Discs[0];
        }

        Discs[] result = new Discs[discs.size()];
        Stack<Discs> tempStack = new Stack<>();

        // Vaciar a temporal
        while (!discs.empty()) {
            tempStack.push(discs.pop());
        }

        // Llenar array y restaurar stack original
        int index = 0;
        while (!tempStack.empty()) {
            Discs disc = tempStack.pop();
            result[index++] = disc;
            discs.push(disc); // Restaurar
        }

        return result;
    }

    /**
     * ✅ Método para obtener representación visual respetando Stack
     */
    public String getStackRepresentation() {
        if (discs.empty()) {
            return "Torre " + name + ": [Vacía]";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Torre ").append(name).append(" (tope → base):\n");

        Stack<Discs> tempStack = new Stack<>();

        // Vaciar mostrando desde tope hacia base
        while (!discs.empty()) {
            Discs disc = discs.pop();
            tempStack.push(disc);
            sb.append("  ").append(disc.toString()).append("\n");
        }

        // Restaurar stack original
        while (!tempStack.empty()) {
            discs.push(tempStack.pop());
        }

        return sb.toString();
    }

    /**
     * Verifica si un movimiento desde esta torre es válido
     */
    public boolean canMoveTo(Tower targetTower) {
        if (this.isEmpty()) {
            return false;
        }

        if (targetTower.isFull()) {
            return false;
        }

        Discs movingDisc = this.peekDisc(); // ✅ Solo peek, no acceso directo
        Discs targetTopDisc = targetTower.peekDisc();

        return movingDisc.canBePlacedOn(targetTopDisc);
    }

    /**
     * ✅ Limpia todos los discos respetando Stack operations
     */
    public void clear() {
        while (!discs.empty()) {
            discs.pop();
        }
    }

    // Getters básicos
    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getBaseWidth() { return baseWidth; }
    public int getMaxCapacity() { return maxCapacity; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        updateAllDiscPositions();
    }

    @Override
    public String toString() {
        return "Torre " + name + " [discos=" + discs.size() +
                ", capacidad=" + maxCapacity + "]";
    }
}