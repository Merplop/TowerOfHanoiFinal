package com.mirohaap.towerofhanoitutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles analytics related to game sessions, such as tracking optimal and unoptimal moves,
 * and calculating elapsed time. It supports reading from and writing to a file to preserve
 * analytics data across sessions.
 */
public class AnalyticsUtil {
    private int previousOptimalMoves = 0;
    private int previousUnoptimalMoves = 0;
    private int previousElapsedTime = 0;
    private static AnalyticsUtil _instance;
    private int optimalMoves = 0;
    private int unoptimalMoves = 0;
    private long elapsedTime = 0;
    private boolean openedThisSession = false;

    private ArrayList<Integer> optimalMovesOverTime = new ArrayList<>();

    /**
     * Private constructor for singleton pattern. It initializes the class by fetching
     * previous session analytics from a file.
     */
    private AnalyticsUtil() {
        fetchPreviousAnalyticData();
    }

    /**
     * Calculates values for the current session by adding previous session data to the
     * current session's data.
     */
    private void calculateValues() {
        optimalMoves = previousOptimalMoves + fetchNumberOfMovesFromCurrentSession(true);
        unoptimalMoves = previousUnoptimalMoves + fetchNumberOfMovesFromCurrentSession(false);
        elapsedTime = previousElapsedTime + fetchInGameTimeFromCurrentSession();
    }

    /**
     * Provides access to the singleton instance of AnalyticsUtil, creating it if necessary
     * and recalculating values for the current session.
     *
     * @return The singleton instance of AnalyticsUtil.
     */
    public static AnalyticsUtil getInstance() {
        if (_instance == null) {
            _instance = new AnalyticsUtil();
        }
        _instance.calculateValues();
        return _instance;
    }

    /**
     * Fetches and loads analytics data from a previous session from the file.
     * If the file does not exist, it creates a new file for future use.
     */
    public void fetchPreviousAnalyticData() {
        try {
            File data = new File("analytics.txt");
            Scanner read = new Scanner(data);
            if (read.hasNextLine()) {
                previousOptimalMoves = Integer.parseInt(read.nextLine());
            }
            if (read.hasNextLine()) {
                previousUnoptimalMoves = Integer.parseInt(read.nextLine());
            }
            if (read.hasNextLine()) {
                previousElapsedTime = Integer.parseInt(read.nextLine());
            }
            while (read.hasNextLine()) {
                String line = read.nextLine();
                optimalMovesOverTime.add(Integer.parseInt(line));
            }
            read.close();
        } catch (FileNotFoundException e) {
            try {
                File data = new File("analytics.txt");
                data.createNewFile();
            } catch (IOException e2) {
                System.out.println("Error creating analytics file.");
                e.printStackTrace();
            }
        }
    }

    /**
     * Writes the analytics data from the current session to the file.
     * This includes optimal and unoptimal move counts, elapsed time, and the history of
     * optimal moves over time.
     */
    public void writeAnalyticDataToFile() {
        try {
            FileWriter myWriter = new FileWriter("analytics.txt");
            myWriter.write(Integer.toString(optimalMoves));
            myWriter.write("\n");
            myWriter.write(Integer.toString(unoptimalMoves));
            myWriter.write("\n");
            myWriter.write(Integer.toString((int) elapsedTime));
            myWriter.write("\n");
            for (Integer in : optimalMovesOverTime) {
                myWriter.write(Integer.toString(in));
                myWriter.write("\n");
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to analytics file.");
            e.printStackTrace();
        }
    }

    /**
     * Logs the number of optimal moves made during the current session.
     */
    public void logOptimalMoves() {
        optimalMovesOverTime.add(optimalMoves);
    }

    /**
     * Fetches the number of moves from the current session, categorized by their optimality.
     *
     * @param moveOptimality True to fetch the count of optimal moves, false for unoptimal moves.
     * @return The number of moves of the specified type made in the current session.
     */
    private int fetchNumberOfMovesFromCurrentSession(boolean moveOptimality) {
        int count = 0;
        ArrayList<Boolean> moves = Repository.getInstance().getOptimalMoves();
        for (Boolean optimal : moves) {
            if (moveOptimality == optimal) {
                ++count;
            }
        }
        return count;
    }

    /**
     * Fetches the elapsed time in seconds from the current game session.
     *
     * @return The elapsed time in seconds during the current session.
     */
    private long fetchInGameTimeFromCurrentSession() {
        return Repository.getInstance().calculateElapsedTime() / 1000;
    }

    /**
     * Gets the total number of optimal moves made across all sessions.
     *
     * @return The total number of optimal moves.
     */
    public int getNumberOfOptimalMoves() {
        return optimalMoves;
    }

    /**
     * Gets the total number of unoptimal moves made across all sessions.
     *
     * @return The total number of unoptimal moves.
     */
    public int getNumberOfUnoptimalMoves() {
        return unoptimalMoves;
    }

    /**
     * Gets the list of optimal moves made over time. Each entry represents the total
     * number of optimal moves made by the end of a game session.
     *
     * @return An ArrayList containing the total number of optimal moves at the end of each session.
     */
    public ArrayList<Integer> getOptimalMovesOverTime() {
        return optimalMovesOverTime;
    }

    /**
     * Gets the total elapsed time in seconds across all sessions including the current session's
     * ongoing time.
     *
     * @return The total elapsed time in seconds.
     */
    public long getElapsedTime() {
        return elapsedTime + fetchInGameTimeFromCurrentSession();
    }
}

