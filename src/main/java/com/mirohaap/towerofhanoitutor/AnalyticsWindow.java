package com.mirohaap.towerofhanoitutor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

/**
 * The AnalyticsWindow class manages the analytics UI for the Tower of Hanoi tutor application.
 * It displays statistics such as total optimal and unoptimal moves, total time spent, and a chart
 * showing the number of optimal moves over time.
 */
public class AnalyticsWindow {

    @FXML
    private Text tutorText, optimalText, unoptimalText, timeText;

    @FXML
    LineChart<Number, Number> pastMovesChart;

    /**
     * Initializes the analytics window with data from the AnalyticsUtil class.
     * This method sets up text fields with the total counts of optimal and unoptimal moves,
     * total time spent, and initializes the line chart with the history of optimal moves.
     */
    @FXML
    private void initialize() {

        if (!Tutor.getInstance().isEnabled()) {
            tutorText.setText("Enable tutor to get optimal move data.");
        } else {
            optimalText.setText("Total optimal moves: " + AnalyticsUtil.getInstance().getNumberOfOptimalMoves());
            unoptimalText.setText("Total un-optimal moves: " + AnalyticsUtil.getInstance().getNumberOfUnoptimalMoves());
        }
        timeText.setText("Total time spent: " + AnalyticsUtil.getInstance().getElapsedTime() + " seconds");

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        ArrayList<Integer> moves = AnalyticsUtil.getInstance().getOptimalMovesOverTime();
        series.setName("Number of Optimal Moves Over Time");

        for (int i = 0; i < moves.size(); i++) {
            series.getData().add(new XYChart.Data(i, moves.get(i)));
        }

        pastMovesChart.getData().add(series);
    }

    /**
     * Opens the analytics window. It loads the FXML for the analytics view,
     * sets the scene, and shows the stage.
     *
     * @throws IOException If there's an issue loading the FXML file.
     */
    public void openWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Window.class.getResource("analytics-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        Stage secondStage = new Stage();
        secondStage.setTitle("Game Analytics");
        secondStage.setScene(scene);
        secondStage.setResizable(false);
        secondStage.setOnCloseRequest(event -> handleCloseBehavior());
        secondStage.show();
    }

    /**
     * Handles the behavior when the analytics window is closed. This includes logging the
     * current session's optimal moves and writing all analytics data to file.
     */
    public void handleCloseBehavior() {
        AnalyticsUtil.getInstance().logOptimalMoves();
        AnalyticsUtil.getInstance().writeAnalyticDataToFile();
    }
}
