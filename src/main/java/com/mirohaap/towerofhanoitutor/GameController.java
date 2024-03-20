package com.mirohaap.towerofhanoitutor;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javafx.stage.Stage;

import org.apache.commons.lang3.mutable.MutableBoolean;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/**
 * Controls the game logic and UI interactions for the Tower of Hanoi tutor application.
 * It handles initialization of game elements, user actions like auto-playing or stepping
 * through the game, and updates the UI based on game state changes.
 */
public class GameController implements PropertyChangeListener {

    @FXML
    private AnchorPane gamePanel;
    @FXML
    private Slider speedSlider;
    @FXML
    private Text secondsDisplay, timeLabel;
    @FXML
    private Button autoPlayButton, backButton, nextButton;
    @FXML
    public TextFlow tutorText;

    private DragDropUtil dragDropUtil;
    private AutoPlayUtil autoPlayUtil;
    private Window window;

    /**
     * Initializes the controller and sets up the UI based on whether the tutor mode is enabled.
     */
    @FXML
    private void initialize() {
        if (!Tutor.getInstance().isEnabled()) {
            speedSlider.setVisible(false);
            autoPlayButton.setVisible(false);
            backButton.setVisible(false);
            nextButton.setVisible(false);
            secondsDisplay.setVisible(false);
            timeLabel.setVisible(false);
        } else {
            backButton.setDisable(true);
        }

        secondsDisplay.textProperty().bind(Bindings.format("%.2f", speedSlider.valueProperty()));
    }

    /**
     * Initializes the rings based on the selected number of rings and adjusts the game
     * environment accordingly.
     *
     * @param ringCount The number of rings selected for the game.
     */
    public void initRings(int ringCount) {
        List<Ring> rings = new ArrayList<>() {{
            for (int i = 1; i <= 10; i++) {
                if (i <= ringCount) {
                    Ring cur = new Ring((StackPane) gamePanel.lookup("#ring" + i), i);
                    cur.getVisualRing().setLayoutY(cur.getVisualRing().getLayoutY() + (29 * (10 - ringCount)));
                    add(cur);
                } else {
                    gamePanel.getChildren().remove(gamePanel.lookup("#ring" + i));
                }
            }
        }};
        Repository.getInstance().init(ringCount);

        if (ringCount < 10) {
            double adjustment = 29 * (10 - ringCount);
            for (int i = 1; i < 4; i++) {
                Rectangle cur = (Rectangle) gamePanel.lookup("#tower" + i);
                cur.setHeight(cur.getHeight() - adjustment);
                cur.setLayoutY(cur.getLayoutY() + adjustment);
            }
        }

        this.dragDropUtil = new DragDropUtil(gamePanel, rings);
        Repository.getInstance().addListener(this);
        AnimationRepository.getInstance().addListener(this);
    }

    /**
     * Handles the restart button click by closing the current game window and opening a new one.
     *
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    public void onRestartButtonClick() throws IOException {
        Stage currentGameStage = (Stage) autoPlayButton.getScene().getWindow();
        currentGameStage.close();
        Window.getInstance().resetGame();
    }

    /**
     * Opens the analytics window to display game statistics.
     *
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    public void showAnalytics() throws IOException {
        AnalyticsWindow aw = new AnalyticsWindow();
        aw.openWindow();
    }

    /**
     * Begins or pauses the auto-play functionality depending on the current state.
     */
    @FXML
    public void beginAutoPlay() {
        if (autoPlayUtil == null && !AnimationRepository.getInstance().animationsRunning()) {
            allowInteractions(false);
            autoPlayUtil = new AutoPlayUtil(dragDropUtil);
            autoPlayUtil.beginPlaying((int) (speedSlider.getValue() * 1000));
            autoPlayButton.setText("Pause");
        } else if (autoPlayUtil != null) {
            autoPlayUtil.stopPlaying();
            autoPlayUtil = null;
            autoPlayButton.setText("AutoPlay");
        }
    }

    /**
     * Advances the game by one move.
     */
    @FXML
    public void stepForward() {
        if (AnimationRepository.getInstance().animationsRunning()) {
            return;
        }
        dragDropUtil.disableUserInput();
        allowInteractions(false);
        Move next = Tutor.getInstance().getNextMove();
        next.setValid(true);
        Repository.getInstance().applyMove(next);
        dragDropUtil.animateMove(next, speedSlider.getValue() * 1000 * 0.9, new MutableBoolean(false));
    }

    /**
     * Reverts the game by one move.
     */
    @FXML
    public void stepBack() {
        if (AnimationRepository.getInstance().animationsRunning()) {
            return;
        }
        dragDropUtil.disableUserInput();
        allowInteractions(false);

        Move last = Repository.getInstance().popLastValidMove();
        Tutor.getInstance().revertMove();

        dragDropUtil.animateMove(last.reversed(), speedSlider.getValue() * 1000 * 0.9, new MutableBoolean(true));
    }

    /**
     * Handles property changes and updates the UI accordingly.
     *
     * @param evt The property change event.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "move":
                if (!AnimationRepository.getInstance().animationsRunning() && autoPlayUtil == null) {
                    backButton.setDisable(!(Repository.getInstance().getValidMoveCount() > 0));
                }
                break;
            case "all_animations_complete":
                updateInterface();
                break;
            case "win":
                allowInteractions(false);
                if (autoPlayUtil != null) {
                    autoPlayUtil.stopPlaying();
                }
                dragDropUtil.disableUserInput();
                gameComplete();
                backButton.setDisable(false);
                break;
        }
    }

    /**
     * Updates the game interface based on the current state.
     */
    private void updateInterface() {
        if (autoPlayUtil != null) {
            speedSlider.setDisable(true);
            return;
        }
        if (Tutor.getInstance().isEnabled()) {
            backButton.setDisable(!(Repository.getInstance().getValidMoveCount() > 0));
            nextButton.setDisable(!Tutor.getInstance().movesLeft());
            autoPlayButton.setDisable(!Tutor.getInstance().movesLeft());
            dragDropUtil.allowUserInput(Tutor.getInstance().movesLeft());
            speedSlider.setDisable(false);
        } else {
            dragDropUtil.allowUserInput(!Repository.getInstance().checkWin());
        }
    }

    /**
     * Allows or disallows user interactions based on the parameter.
     *
     * @param canInteract Whether interactions should be allowed.
     */
    private void allowInteractions(boolean canInteract) {
        speedSlider.setDisable(!canInteract);
        backButton.setDisable(!canInteract);
        nextButton.setDisable(!canInteract);
    }

    /**
     * Displays a game completion alert to the user.
     */
    public void gameComplete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "You won the game!", ButtonType.FINISH);
        alert.showAndWait();
    }

    /**
     * Displays a given message in the tutor text area.
     *
     * @param message The message to display.
     */
    public void textToDisplay(String message) {
        tutorText.getChildren().clear();
        Text text = new Text(message);
        text.setFont(Font.font(20));
        tutorText.getChildren().add(text);
    }

    /**
     * Sets the window associated with this controller.
     *
     * @param window The window to set.
     */
    public void setWindow(Window window) {
        this.window = window;
    }

    /**
     * Handles the restart button click action.
     *
     * @throws IOException If an I/O error occurs.
     */
    @FXML
    public void onRestartButtonCLick() throws IOException {
        window.resetGame();
    }
}