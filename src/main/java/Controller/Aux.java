package Controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase que maneja la persistencia de datos
 * Guarda y lee el historial de movimientos en archivos de acceso aleatorio (.bin)
 */
public class Aux {

    private static final String HISTORY_DIRECTORY = "hanoi_history";
    private static final String FILE_EXTENSION = ".bin";
    private static final String TEXT_EXTENSION = ".txt";

    /**
     * Constructor de Connection
     * Crea el directorio de historial si no existe
     */
    public Aux() {
        createHistoryDirectory();
    }

    /**
     * Crea el directorio para almacenar los historiales
     */
    private void createHistoryDirectory() {
        try {
            Path historyPath = Paths.get(HISTORY_DIRECTORY);
            if (!Files.exists(historyPath)) {
                Files.createDirectories(historyPath);
            }
        } catch (IOException e) {
            System.err.println("Error al crear directorio de historial: " + e.getMessage());
        }
    }

    /**
     * Guarda el historial de movimientos en archivo binario
     * @param moveHistory Lista de movimientos
     * @param discCount Número de discos del juego
     * @param totalMoves Total de movimientos realizados
     * @return Nombre del archivo guardado
     * @throws IOException Si hay error en la escritura
     */
    public String saveHistoryToBinary(List<String> moveHistory, int discCount, int totalMoves) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = HISTORY_DIRECTORY + File.separator +
                "hanoi_" + discCount + "discos_" + timestamp + FILE_EXTENSION;

        try (RandomAccessFile file = new RandomAccessFile(filename, "rw")) {
            // Escribir cabecera del archivo
            writeHeader(file, discCount, totalMoves, moveHistory.size());

            // Escribir cada movimiento
            for (String move : moveHistory) {
                writeMove(file, move);
            }
        }

        return filename;
    }

    /**
     * Guarda el historial de movimientos en archivo de texto
     * @param moveHistory Lista de movimientos
     * @param discCount Número de discos del juego
     * @param totalMoves Total de movimientos realizados
     * @param gameState Estado final del juego
     * @return Nombre del archivo guardado
     * @throws IOException Si hay error en la escritura
     */
    public String saveHistoryToText(List<String> moveHistory, int discCount, int totalMoves, String gameState) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = HISTORY_DIRECTORY + File.separator +
                "hanoi_" + discCount + "discos_" + timestamp + TEXT_EXTENSION;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            // Escribir cabecera
            writer.write("=== TORRES DE HANOI - HISTORIAL DE SIMULACIÓN ===\n");
            writer.write("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n");
            writer.write("Número de discos: " + discCount + "\n");
            writer.write("Total de movimientos: " + totalMoves + "\n");
            writer.write("Movimientos mínimos: " + ((int) Math.pow(2, discCount) - 1) + "\n");
            writer.write("Eficiencia: " + (totalMoves == (int) Math.pow(2, discCount) - 1 ? "ÓPTIMA" : "NO ÓPTIMA") + "\n");
            writer.write("================================================\n\n");

            // Escribir historial de movimientos
            writer.write("HISTORIAL DE MOVIMIENTOS:\n");
            writer.write("------------------------\n");
            for (String move : moveHistory) {
                writer.write(move + "\n");
            }

            // Escribir estado final del juego
            writer.write("\nESTADO FINAL DEL JUEGO:\n");
            writer.write("----------------------\n");
            writer.write(gameState);
        }

        return filename;
    }

    /**
     * Escribe la cabecera del archivo binario
     * @param file Archivo de acceso aleatorio
     * @param discCount Número de discos
     * @param totalMoves Total de movimientos
     * @param historySize Tamaño del historial
     * @throws IOException Si hay error en la escritura
     */
    private void writeHeader(RandomAccessFile file, int discCount, int totalMoves, int historySize) throws IOException {
        // Escribir timestamp
        file.writeLong(System.currentTimeMillis());

        // Escribir información del juego
        file.writeInt(discCount);
        file.writeInt(totalMoves);
        file.writeInt(historySize);

        // Escribir movimientos mínimos
        file.writeInt((int) Math.pow(2, discCount) - 1);
    }

    /**
     * Escribe un movimiento en el archivo binario
     * @param file Archivo de acceso aleatorio
     * @param move Movimiento a escribir
     * @throws IOException Si hay error en la escritura
     */
    private void writeMove(RandomAccessFile file, String move) throws IOException {
        byte[] moveBytes = move.getBytes("UTF-8");
        file.writeInt(moveBytes.length);  // Escribir longitud del string
        file.write(moveBytes);            // Escribir el string
    }

    /**
     * Lee el historial desde un archivo binario
     * @param filename Nombre del archivo a leer
     * @return GameHistoryData con la información leída
     * @throws IOException Si hay error en la lectura
     */
    public GameHistoryData readHistoryFromBinary(String filename) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filename, "r")) {
            // Leer cabecera
            long timestamp = file.readLong();
            int discCount = file.readInt();
            int totalMoves = file.readInt();
            int historySize = file.readInt();
            int minimumMoves = file.readInt();

            // Leer movimientos
            List<String> moveHistory = new ArrayList<>();
            for (int i = 0; i < historySize; i++) {
                int moveLength = file.readInt();
                byte[] moveBytes = new byte[moveLength];
                file.readFully(moveBytes);
                moveHistory.add(new String(moveBytes, "UTF-8"));
            }

            return new GameHistoryData(timestamp, discCount, totalMoves, minimumMoves, moveHistory);
        }
    }

    /**
     * Obtiene la lista de archivos de historial disponibles
     * @return Lista de nombres de archivos
     */
    public List<String> getAvailableHistoryFiles() {
        List<String> files = new ArrayList<>();
        Path historyPath = Paths.get(HISTORY_DIRECTORY);

        if (Files.exists(historyPath)) {
            try {
                Files.list(historyPath)
                        .filter(path -> path.toString().endsWith(FILE_EXTENSION) ||
                                path.toString().endsWith(TEXT_EXTENSION))
                        .forEach(path -> files.add(path.getFileName().toString()));
            } catch (IOException e) {
                System.err.println("Error al leer archivos de historial: " + e.getMessage());
            }
        }

        return files;
    }

    /**
     * Elimina un archivo de historial
     * @param filename Nombre del archivo a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean deleteHistoryFile(String filename) {
        try {
            Path filePath = Paths.get(HISTORY_DIRECTORY, filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Error al eliminar archivo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Limpia todos los archivos de historial antiguos (más de 30 días)
     * @return Número de archivos eliminados
     */
    public int cleanOldHistoryFiles() {
        AtomicInteger deletedCount = new AtomicInteger();
        Path historyPath = Paths.get(HISTORY_DIRECTORY);

        if (Files.exists(historyPath)) {
            try {
                long thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000);

                Files.list(historyPath)
                        .filter(path -> {
                            try {
                                return Files.getLastModifiedTime(path).toMillis() < thirtyDaysAgo;
                            } catch (IOException e) {
                                return false;
                            }
                        })
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                                deletedCount.getAndIncrement();
                            } catch (IOException e) {
                                System.err.println("Error al eliminar archivo antiguo: " + e.getMessage());
                            }
                        });
            } catch (IOException e) {
                System.err.println("Error al limpiar archivos antiguos: " + e.getMessage());
            }
        }

        return deletedCount.get();
    }

    /**
     * Clase interna para almacenar datos del historial leído
     */
    public static class GameHistoryData {
        private final long timestamp;
        private final int discCount;
        private final int totalMoves;
        private final int minimumMoves;
        private final List<String> moveHistory;

        public GameHistoryData(long timestamp, int discCount, int totalMoves,
                               int minimumMoves, List<String> moveHistory) {
            this.timestamp = timestamp;
            this.discCount = discCount;
            this.totalMoves = totalMoves;
            this.minimumMoves = minimumMoves;
            this.moveHistory = new ArrayList<>(moveHistory);
        }

        // Getters
        public long getTimestamp() { return timestamp; }
        public int getDiscCount() { return discCount; }
        public int getTotalMoves() { return totalMoves; }
        public int getMinimumMoves() { return minimumMoves; }
        public List<String> getMoveHistory() { return new ArrayList<>(moveHistory); }

        public String getFormattedDate() {
            return LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.ofTotalSeconds(ZoneId.systemDefault().getRules().getOffset(Instant.now()).getTotalSeconds()))
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }

        public boolean isOptimal() {
            return totalMoves == minimumMoves;
        }
    }
}
