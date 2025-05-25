package Unicordoba;

import Controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Iniciar directamente el controlador con el escenario principal
            new Controller(primaryStage);
        } catch (Exception ex) {
            System.err.println("Error al iniciar la aplicación: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Iniciando Torres de Hanoi...");
        launch(args);
    }
}