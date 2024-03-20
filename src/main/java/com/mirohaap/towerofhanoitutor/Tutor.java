package com.mirohaap.towerofhanoitutor;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;

import java.util.ArrayList;


/**
 * The {@code Tutor} class represents a tutor for the Tower of Hanoi puzzle,
 * offering guidance through the puzzle by calculating and providing the best moves.
 * This class utilizes the FreeTTS library to vocalize instructions or feedback
 * to the user. It implements a singleton pattern to ensure only one instance of
 * the tutor is active at any time.
 * <p>
 * The class calculates the optimal move sequence for a given number of rings and
 * provides real-time feedback to the user about the correctness of their moves.
 * Vocal feedback is provided through a text-to-speech engine, aiming to guide the
 * user to the solution with auditory cues.
 * </p>
 */
public class Tutor {

    private static Tutor _instance; // Singleton instance of the Tutor
    private boolean enabled = false; // Flag to enable or disable tutor feedback
    private ArrayList<Move> bestMoves = new ArrayList<>(); // List of calculated best moves
    private int moveNumber = 0; // Index for the current move in the bestMoves list
    private volatile boolean isSpeaking = false; // Flag to prevent overlapping speech threads
    private GameController controller;
    Voice voice; // Voice object for text-to-speech functionality

    /**
     * Private constructor for the Tutor class.
     * Initializes the text-to-speech engine and vocalizes an introductory message.
     * This constructor is private to enforce the singleton pattern.
     */


    private Tutor() {
        initializeVoice();
    }

    /**
     * Calculates the optimal sequence of moves for solving the Tower of Hanoi puzzle
     * with a specified number of rings.
     *
     * @param numRings the number of rings in the Tower of Hanoi puzzle
     */
    public void calculateMoves(int numRings) {
        computeBestMoves(numRings, 1, 3, 2);
    }

    /**
     * Initializes the FreeTTS voice manager and selects a specific voice for speech synthesis.
     * This method sets various parameters to customize the voice's pitch, rate, and volume.
     */
    private void initializeVoice() {
        VoiceManager voiceManager = VoiceManager.getInstance();
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        voice = voiceManager.getVoice("kevin16");
        voice.allocate();
        voice.setRate(110);
        voice.setPitch(100);
        voice.setVolume(0.8f);
    }


    /**
     * Provides auditory feedback to the user by vocalizing a given message.
     * If the tutor is already speaking, subsequent calls to this method will be ignored
     * to prevent overlapping speech.
     *
     * @param message the message to be vocalized
     */

    public void speak(String message) {
        if (isSpeaking) {
            return;
        }

        new Thread(() -> {
            try {
                isSpeaking = true;
                voice.speak(message);
            } finally {
                isSpeaking = false;
            }
        }).start();
    }

    /**
     * Checks if a given move is the best move according to the pre-calculated sequence.
     * If the move is not the best move, it provides vocal feedback indicating the correct move.
     *
     * @param move the move made by the user
     * @return {@code true} if the move is the best move, {@code false} otherwise
     */
    public boolean isBestMove(Move move) {
        if (!enabled) {
            return true;
        }
        if (!move.equals(bestMoves.get(moveNumber))) {
            speak(bestMoves.get(moveNumber).toString());
            controller.textToDisplay(bestMoves.get(moveNumber).toString());
            return false;
        }

        moveNumber += 1;
        return true;
    }

    /**
     * Validates a move made by the user against the pre-calculated best moves.
     * The validation result is also set in the move object.
     *
     * @param move the move to validate
     * @return {@code true} if the move is valid, {@code false} otherwise
     */
    public boolean validateMove(Move move) {
        if (bestMoves.isEmpty()) {
            throw new RuntimeException("Tutor validation called before calculateMoves called!");
        }

        boolean moveStatus = isBestMove(move);
        move.setValid(moveStatus);
        return moveStatus;
    }

    /**
     * Returns whether the tutor is currently enabled.
     *
     * @return {@code true} if the tutor is enabled, {@code false} otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Disables the tutor, preventing it from providing feedback.
     */
    public void disable() {
        enabled = false;
    }

    /**
     * Enables the tutor, allowing it to provide feedback.
     */
    public void enable() {
        enabled = true;
        speak("Im here to help! Play when you are ready!");
    }


    /**
     * Adds a best move to the sequence of best moves for solving the puzzle.
     *
     * @param n    the number of the ring being moved
     * @param from the starting rod
     * @param to   the destination rod
     */
    public void addBestMove(int n, int from, int to) {
        Move move = new Move(n, from, to);
        bestMoves.add(move);
    }

    public Move getNextMove() {
        if (bestMoves.isEmpty()) {
            throw new RuntimeException("Tutor called before calculateMoves called!");
        }

        moveNumber += 1;
        return bestMoves.get(moveNumber - 1);

    }


    /**
     * Recursively computes the optimal sequence of moves for solving the Tower of Hanoi puzzle.
     * This method is called internally by {@code calculateMoves}.
     *
     * @param n        the number of rings to move
     * @param from_rod the starting rod
     * @param to_rod   the destination rod
     * @param aux_rod  the auxiliary rod
     */

    private void computeBestMoves(int n, int from_rod, int to_rod, int aux_rod) {
        if (n == 1) {
            addBestMove(1, from_rod, to_rod);
            return;
        }
        computeBestMoves(n - 1, from_rod, aux_rod, to_rod);
        addBestMove(n, from_rod, to_rod);
        computeBestMoves(n - 1, aux_rod, to_rod, from_rod);
    }

    public void revertMove() {
        if (moveNumber > 0) {
            moveNumber--;
        } else {
            throw new RuntimeException("There are no more moves to revert!");
        }
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public void setController(GameController gameController) {
        controller = gameController;
    }


    public boolean movesLeft() {
        return !(moveNumber == bestMoves.size());
    }


    /**
     * Returns the singleton instance of the Tutor class.
     * If the instance does not exist, it is created.
     *
     * @return the singleton instance of the Tutor
     */
    public static Tutor getInstance() {
        if (_instance == null) {
            _instance = new Tutor();
        }
        return _instance;
    }

    public ArrayList<Move> getBestMoves() {
        return bestMoves;
    }
}
