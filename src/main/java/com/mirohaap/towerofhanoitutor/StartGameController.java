package com.mirohaap.towerofhanoitutor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller class for the start game UI of the Tower of Hanoi tutor application.
 * Manages the interactions with UI components like the start button, ring counter spinner, and tutor mode checkbox.
 */
public class StartGameController {
    @FXML
    private Button startButton;
    @FXML
    private Spinner<Integer> ringCounter;
    @FXML
    private CheckBox tutorCheckBox;

    /**
     * Initializes the controller, setting up the ring counter spinner with values from 3 to 10 and default value 6.
     */
    @FXML
    private void initialize() {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(3, 10, 6);
        ringCounter.setValueFactory(valueFactory);
        valueFactory.setWrapAround(true);
    }

    /**
     * Handles the action when the start game button is clicked.
     * This method sets up the game based on the selected options and launches the main game window.
     *
     * @throws IOException If there is an error loading the game-view FXML.
     */
    @FXML
    private void startGameClicked() throws IOException {
        // Enable or disable the tutor based on the checkbox
        if (tutorCheckBox.isSelected()) {
            Tutor.getInstance().enable();
        } else {
            Tutor.getInstance().disable();
        }

        // Prepare and show the game stage
        Stage gameStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("game-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        GameController gameController = fxmlLoader.getController();

        // If tutor mode is enabled, display the helper text
        if (tutorCheckBox.isSelected()) {
            gameController.textToDisplay("I'm here to help! Play when you are ready!");
        }

        gameStage.setTitle("Tower of Hanoi");
        gameStage.setScene(scene);
        gameStage.setResizable(false);
        gameStage.show();

        // Initialize the game with the selected number of rings
        int numRings = ringCounter.getValue();
        gameController.initRings(numRings);

        // Pass the game controller to the tutor for further interactions
        Tutor.getInstance().setController(gameController);
        Tutor.getInstance().calculateMoves(numRings);

        // Close the current (start game) window
        Stage currentStage = (Stage) startButton.getScene().getWindow();
        currentStage.close();
    }
}
