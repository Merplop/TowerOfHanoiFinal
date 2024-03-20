package com.mirohaap.towerofhanoitutor;

/**
 * The Move class represents a move in the Tower of Hanoi game.
 * It encapsulates the details of a move, including the disk number,
 * the source rod, and the destination rod.
 */
public class Move {
    private int n;
    private int from;
    private int to;
    private boolean valid;

    public Move(int n, int from, int to, boolean valid) {
        this.n = n;
        this.from = from;
        this.to = to;
        this.valid = valid;
    }

    public Move(int n, int from, int to) {
        this(n, from, to, false);
    }

    public void setValid(boolean val) {
        valid = val;
    }

    public boolean isValid() {
        return valid;
    }


    /**
     * Returns the disk number of the move.
     *
     * @return the disk number
     */
    public int getN() {
        return n;
    }

    /**
     * Sets the disk number of the move.
     *
     * @param n the disk number to set
     * @return the Move object itself for method chaining
     */
    public Move setN(int n) {
        this.n = n;
        return this;
    }

    /**
     * Returns the source rod of the move.
     *
     * @return the source rod
     */
    public int getFrom() {
        return from;
    }

    /**
     * Sets the source rod of the move.
     *
     * @param from the source rod to set
     * @return the Move object itself for method chaining
     */
    public Move setFrom(int from) {
        this.from = from;
        return this;
    }

    /**
     * Returns the destination rod of the move.
     *
     * @return the destination rod
     */
    public int getTo() {
        return to;
    }

    /**
     * Sets the destination rod of the move.
     *
     * @param to the destination rod to set
     * @return the Move object itself for method chaining
     */
    public Move setTo(int to) {
        this.to = to;
        return this;
    }

    public Move reversed() {
        return new Move(n, to, from, valid);
    }

    /**
     * Returns a string representation of the Move object.
     *
     * @return a string representation of the Move object
     */
    @Override
    public String toString() {
        return "Move ring " + getN() + " from tower " + getFrom() + " to tower " + getTo();
    }

    /**
     * Checks if the Move object is equal to another object.
     *
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Move attemptedMove)) {
            return false;
        }

        return this.getN() == attemptedMove.getN() && this.getTo() == attemptedMove.getTo() && this.getFrom() == attemptedMove.getFrom();
    }
}