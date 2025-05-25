package Methods.Models;

import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

/**
 * Clase que representa una torre en el juego de Torres de Hanoi
 * Utiliza una Stack para manejar los discos siguiendo LIFO
 */
public class Tower {
    private Stack<Discs> discs;    // Pila de discos
    private String name;           // Nombre de la torre (A, B, C)
    private double x;              // Posición X de la torre
    private double y;              // Posición Y de la base de la torre
    private double baseWidth;      // Ancho de la base de la torre
    private double height;         // Altura de la torre
    private int maxCapacity;       // Capacidad máxima de discos

    public static final double TOWER_HEIGHT = 300.0;
    public static final double TOWER_BASE_WIDTH = 200.0;

    /**
     * Constructor de la torre
     * @param name Nombre identificador de la torre
     * @param x Posición X
     * @param y Posición Y
     * @param maxCapacity Capacidad máxima de discos
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
     * Constructor simplificado con capacidad por defecto
     * @param name Nombre de la torre
     * @param x Posición X
     * @param y Posición Y
     */
    public Tower(String name, double x, double y) {
        this(name, x, y, 6); // Capacidad máxima por defecto
    }

    /**
     * Intenta colocar un disco en la torre
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

        // Verificar regla del juego: no disco grande sobre disco pequeño
        if (!discs.isEmpty()) {
            Discs topDisc = discs.peek();
            if (!disc.canBePlacedOn(topDisc)) {
                return false;
            }
        }

        // Colocar el disco
        discs.push(disc);
        updateDiscPosition(disc);
        return true;
    }

    /**
     * Remueve y retorna el disco superior
     * @return Disco removido, null si la torre está vacía
     */
    public Discs popDisc() {
        if (discs.isEmpty()) {
            return null;
        }
        return discs.pop();
    }

    /**
     * Obtiene el disco superior sin removerlo
     * @return Disco superior, null si está vacía
     */
    public Discs peekDisc() {
        if (discs.isEmpty()) {
            return null;
        }
        return discs.peek();
    }

    /**
     * Actualiza la posición visual del disco según su posición en la torre
     * @param disc Disco a posicionar
     */
    private void updateDiscPosition(Discs disc) {
        if (disc == null) return;

        // Calcular posición X (centrado en la torre)
        double discX = x + (baseWidth - disc.getWidth()) / 2;

        // Calcular posición Y (desde la base hacia arriba)
        double discY = y - (discs.size() * disc.getHeight());

        disc.setPosition(discX, discY);
    }

    /**
     * Actualiza las posiciones de todos los discos en la torre
     */
    public void updateAllDiscPositions() {
        for (int i = 0; i < discs.size(); i++) {
            Discs disc = discs.get(i);
            double discX = x + (baseWidth - disc.getWidth()) / 2;
            double discY = y - ((i + 1) * disc.getHeight());
            disc.setPosition(discX, discY);
        }
    }

    /**
     * Verifica si la torre está vacía
     * @return true si está vacía
     */
    public boolean isEmpty() {
        return discs.isEmpty();
    }

    /**
     * Obtiene el número de discos en la torre
     * @return Cantidad de discos
     */
    public int getDiscCount() {
        return discs.size();
    }

    /**
     * Verifica si la torre está llena
     * @return true si alcanzó la capacidad máxima
     */
    public boolean isFull() {
        return discs.size() >= maxCapacity;
    }

    /**
     * Obtiene una lista de todos los discos (del fondo hacia arriba)
     * @return Lista de discos
     */
    public List<Discs> getAllDiscs() {
        return new ArrayList<>(discs);
    }

    /**
     * Calcula la altura ocupada por los discos
     * @return Altura total ocupada
     */
    public double getOccupiedHeight() {
        return discs.size() * (discs.isEmpty() ? 0 : discs.get(0).getHeight());
    }

    /**
     * Obtiene la posición Y donde se colocaría el próximo disco
     * @return Coordenada Y para el próximo disco
     */
    public double getNextDiscY() {
        return y - ((discs.size() + 1) * (discs.isEmpty() ? 20.0 : discs.get(0).getHeight()));
    }

    /**
     * Limpia todos los discos de la torre
     */
    public void clear() {
        discs.clear();
    }

    /**
     * Verifica si un movimiento desde esta torre es válido
     * @param targetTower Torre destino
     * @return true si el movimiento es válido
     */
    public boolean canMoveTo(Tower targetTower) {
        if (this.isEmpty()) {
            return false; // No hay disco para mover
        }

        if (targetTower.isFull()) {
            return false; // Torre destino llena
        }

        Discs movingDisc = this.peekDisc();
        Discs targetTopDisc = targetTower.peekDisc();

        return movingDisc.canBePlacedOn(targetTopDisc);
    }

    // Getters
    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getBaseWidth() {
        return baseWidth;
    }

    public double getHeight() {
        return height;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    // Setters
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
        updateAllDiscPositions();
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Torre ").append(name).append(" [");
        sb.append("discos=").append(discs.size());
        sb.append(", capacidad=").append(maxCapacity);
        sb.append(", posición=(").append(x).append(",").append(y).append(")");
        sb.append("]");
        return sb.toString();
    }

    /**
     * Representación detallada de la torre y sus discos
     * @return String con información completa
     */
    public String getDetailedInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("Torre ").append(name).append(":\n");
        if (discs.isEmpty()) {
            sb.append("  [Vacía]\n");
        } else {
            for (int i = discs.size() - 1; i >= 0; i--) {
                sb.append("  ").append(discs.get(i)).append("\n");
            }
        }
        return sb.toString();
    }
}
