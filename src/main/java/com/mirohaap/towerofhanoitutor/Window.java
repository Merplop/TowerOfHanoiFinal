package com.mirohaap.towerofhanoitutor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the Tower of Hanoi tutor. It extends the JavaFX Application class
 * and manages the main window of the application.
 */
public class Window extends Application {
    private Stage primaryStage;
    private static Window _window;

    /**
     * Starts the application by setting up the primary stage and showing the start screen.
     *
     * @param primaryStage The primary stage for this application, onto which the application scene can be set.
     * @throws IOException If loading the FXML for the start screen fails.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        _window = this;
        showStartScreen();
    }

    /**
     * Provides access to the Window instance, following the Singleton pattern to ensure only one instance is used.
     *
     * @return The single instance of Window.
     */
    public static Window getInstance() {
        return _window;
    }

    /**
     * Loads and displays the start screen from its FXML file.
     *
     * @throws IOException If the FXML file for the start screen cannot be loaded.
     */
    public void showStartScreen() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("start-game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 164, 150);
        primaryStage.setTitle("Tower of Hanoi - New Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Resets the game to the start screen, effectively restarting the application.
     *
     * @throws IOException If the FXML file for the start screen cannot be loaded.
     */
    public void resetGame() throws IOException {
        showStartScreen();
    }

    /**
     * The main entry point for all JavaFX applications. The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * @param args the command line arguments passed to the application.
     *             An application may get these parameters using the getParameters() method.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
