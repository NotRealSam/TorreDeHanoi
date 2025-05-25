package Methods.Models;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Clase que representa un disco en el juego de Torres de Hanoi
 * Cada disco tiene un tamaño/valor único y propiedades visuales
 */
public class Discs {
    private int size;           // Tamaño del disco (1 = más pequeño)
    private Color color;        // Color del disco
    private Rectangle visual;   // Representación visual del disco
    private double width;       // Ancho visual del disco
    private double height;      // Alto visual del disco
    private static final double BASE_WIDTH = 40.0;  // Ancho base
    private static final double DISC_HEIGHT = 20.0; // Alto estándar

    /**
     * Constructor del disco
     * @param size Tamaño del disco (1 es el más pequeño)
     */
    public Discs(int size) {
        this.size = size;
        this.height = DISC_HEIGHT;
        this.width = BASE_WIDTH + (size * 30); // Cada tamaño añade 30px de ancho
        this.color = generateColor(size);
        createVisualRepresentation();
    }

    /**
     * Crea la representación visual del disco
     */
    private void createVisualRepresentation() {
        visual = new Rectangle(width, height);
        visual.setFill(color);
        visual.setStroke(Color.BLACK);
        visual.setStrokeWidth(2);
        visual.setArcWidth(10);
        visual.setArcHeight(10);
    }

    /**
     * Genera un color único para cada tamaño de disco
     * @param size Tamaño del disco
     * @return Color asignado al disco
     */
    private Color generateColor(int size) {
        Color[] colors = {
                Color.RED,      // Tamaño 1
                Color.BLUE,     // Tamaño 2
                Color.GREEN,    // Tamaño 3
                Color.YELLOW,   // Tamaño 4
                Color.PURPLE,   // Tamaño 5
                Color.ORANGE    // Tamaño 6
        };

        if (size >= 1 && size <= colors.length) {
            return colors[size - 1];
        }
        return Color.GRAY; // Color por defecto
    }

    // Getters
    public int getSize() {
        return size;
    }

    public Color getColor() {
        return color;
    }

    public Rectangle getVisual() {
        return visual;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /**
     * Establece la posición visual del disco
     * @param x Coordenada X
     * @param y Coordenada Y
     */
    public void setPosition(double x, double y) {
        visual.setX(x);
        visual.setY(y);
    }

    /**
     * Obtiene la coordenada X del disco
     * @return Coordenada X
     */
    public double getX() {
        return visual.getX();
    }

    /**
     * Obtiene la coordenada Y del disco
     * @return Coordenada Y
     */
    public double getY() {
        return visual.getY();
    }

    /**
     * Verifica si este disco puede ser colocado sobre otro disco
     * @param otherDisc El otro disco
     * @return true si puede ser colocado, false en caso contrario
     */
    public boolean canBePlacedOn(Discs otherDisc) {
        if (otherDisc == null) {
            return true; // Puede ser colocado en una torre vacía
        }
        return this.size < otherDisc.size;
    }

    /**
     * Crea una copia del disco (útil para operaciones sin afectar el original)
     * @return Nueva instancia de Discs con las mismas propiedades
     */
    public Discs copy() {
        return new Discs(this.size);
    }

    @Override
    public String toString() {
        return "Disco[tamaño=" + size + ", ancho=" + width + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Discs disc = (Discs) obj;
        return size == disc.size;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(size);
    }
}