package com.mirohaap.towerofhanoitutor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Repository class for managing the state of the Tower of Hanoi game.
 * It keeps track of the towers, moves, and checks for optimal moves.
 */
public class Repository {
    private static Repository _instance;
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);
    // For each tower, [0] is the bottom, last index is the top.
    private List<Stack<Integer>> towers;
    private ArrayList<Boolean> optimalMoves = new ArrayList<>();
    private Stack<Move> moves;
    private boolean initialized;
    private long startTime;

    /**
     * Private constructor for Singleton pattern.
     */
    private Repository() {
        towers = new ArrayList<>();
        moves = new Stack<>();
        initialized = false;
        startTime = System.currentTimeMillis();
    }

    /**
     * Verifies if the move made is optimal by comparing it to a list of best moves.
     *
     * @param move The move to verify.
     */
    public void verifyOptimal(Move move) {
        ArrayList<Move> bestMoves = Tutor.getInstance().getBestMoves();
        if (bestMoves.get(Tutor.getInstance().getMoveNumber() - 1).equals(move)) {
            optimalMoves.add(true);
        } else {
            optimalMoves.add(false);
        }
    }

    /**
     * Calculates elapsed time since the start of the game.
     *
     * @return The elapsed time in milliseconds.
     */
    public long calculateElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Adds a property change listener to this repository.
     *
     * @param listener The listener to add.
     */
    public void addListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    /**
     * Initializes the towers with a specified number of rings.
     *
     * @param ringCount The number of rings to initialize the first tower with.
     */
    public void init(int ringCount) {
        towers.clear();
        for (int i = 0; i < 3; i++) {
            towers.add(new Stack<>());
        }
        for (int i = ringCount; i > 0; i--) {
            towers.get(0).push(i);
        }
        initialized = true;
    }

    /**
     * Returns a copy of the tower at the specified index.
     *
     * @param index The index of the tower.
     * @return A copy of the tower as a List.
     */
    public List<Integer> getTowerByIndex(int index) {
        if (index < towers.size() && index >= 0) {
            return new ArrayList<>(towers.get(index));
        }
        throw new IndexOutOfBoundsException("Towers must be referenced using indexes 0, 1, or 2.");
    }

    /**
     * Returns a list of the top elements of each tower.
     *
     * @return A list containing the top elements of each tower.
     */
    public List<Integer> getTops() {
        List<Integer> tops = new ArrayList<>();
        for (Stack<Integer> tower : towers) {
            if (tower.isEmpty()) {
                tops.add(-1);
            } else {
                tops.add(tower.peek());
            }
        }
        return tops;
    }

    /**
     * Applies a move to the towers.
     *
     * @param move The move to apply.
     */
    public void applyMove(Move move) {
        if (move.isValid()) {
            Integer ring = towers.get(move.getFrom() - 1).pop();
            towers.get(move.getTo() - 1).push(ring);
        }
        logMove(move);
    }

    /**
     * Checks if the game has been won.
     *
     * @return true if the game is won, false otherwise.
     */
    public boolean checkWin() {
        return (towers.get(0).isEmpty() && towers.get(2).isEmpty()) || (towers.get(0).isEmpty() && towers.get(1).isEmpty());
    }

    /**
     * Logs a move and checks for win condition.
     *
     * @param move The move to log.
     */
    private void logMove(Move move) {
        moves.push(move);
        if (Tutor.getInstance().isEnabled()) {
            verifyOptimal(move);
        }
        if (!checkWin()) {
            changes.firePropertyChange("move", null, move);
        } else {
            changes.firePropertyChange("win", null, move);
        }
    }

    /**
     * Pops the last valid move from the move stack, reverting the move.
     *
     * @return The last valid move that was made.
     */
    public Move popLastValidMove() {
        Move move;
        do {
            if (moves.isEmpty()) {
                throw new RuntimeException("No valid moves have been logged yet!");
            }
            move = moves.pop();
        } while (!move.isValid());

        Integer ring = towers.get(move.getTo() - 1).pop();
        towers.get(move.getFrom() - 1).push(ring);
        return move;
    }

    /**
     * Gets the total number of moves made, including invalid ones.
     *
     * @return The total number of moves.
     */
    public int getTotalMoveCount() {
        return moves.size();
    }

    /**
     * Gets the count of valid moves made.
     *
     * @return The number of valid moves.
     */
    public int getValidMoveCount() {
        return (int) moves.stream().filter(Move::isValid).count();
    }

    /**
     * Gets the count of invalid moves made.
     *
     * @return The number of invalid moves.
     */
    public int getInvalidMoveCount() {
        return (int) moves.stream().filter(m -> !m.isValid()).count();
    }

    /**
     * Returns the stack of moves made.
     *
     * @return The stack of moves.
     */
    public Stack<Move> getMoves() {
        return moves;
    }

    /**
     * Checks if the given number is at the top of any tower.
     *
     * @param num The number to check.
     * @return true if the number is at the top of any tower, false otherwise.
     */
    public boolean isTop(Integer num) {
        for (Stack<Integer> tower : towers) {
            if (!tower.isEmpty() && tower.peek().equals(num)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the tower that a given ring number is on.
     *
     * @param num The ring number to find.
     * @return The index of the tower the ring is on, or -1 if not found.
     */
    public int getTower(Integer num) {
        for (int i = 0; i < towers.size(); i++) {
            if (towers.get(i).contains(num)) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Returns the singleton instance of the Repository.
     *
     * @return The singleton instance.
     */
    public static Repository getInstance() {
        if (_instance == null) {
            _instance = new Repository();
        } else if (!_instance.initialized) {
            throw new RuntimeException("Repository accessed before being initialized");
        }
        return _instance;
    }

    /**
     * Resets the repository to its initial state.
     */
    public void reset() {
        if (!initialized) {
            throw new IllegalStateException("Repository must be initialized");
        }
        towers.clear();
        moves.clear();
        optimalMoves.clear();
        initialized = false; // Consider whether you want to de-initialize the repository here.
        changes.firePropertyChange("reset", null, null);
    }

    /**
     * Gets the list of whether moves were optimal.
     *
     * @return A list indicating whether each move was optimal.
     */
    public ArrayList<Boolean> getOptimalMoves() {
        return optimalMoves;
    }
}
