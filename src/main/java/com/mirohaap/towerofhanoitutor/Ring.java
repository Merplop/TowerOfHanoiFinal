package com.mirohaap.towerofhanoitutor;

import javafx.scene.layout.StackPane;

/**
 * Represents a ring in the Tower of Hanoi game, including its visual representation and numerical identifier.
 */
public class Ring {
    private StackPane visualRing; // The visual representation of the ring.
    private int num; // The numerical identifier of the ring.

    /**
     * Constructs a new Ring with a visual representation and a numerical identifier.
     *
     * @param visualRing The StackPane representing the ring's visual appearance.
     * @param num        The numerical identifier of the ring.
     */
    public Ring(StackPane visualRing, int num) {
        this.visualRing = visualRing;
        this.num = num;
    }

    /**
     * Returns a string representation of the Ring, including its visual representation and numerical identifier.
     *
     * @return A string representation of the Ring.
     */
    @Override
    public String toString() {
        return "Ring{" + "visualRing=" + visualRing + ", num=" + num + '}';
    }

    /**
     * Gets the visual representation of the ring.
     *
     * @return The StackPane representing the ring's visual appearance.
     */
    public StackPane getVisualRing() {
        return visualRing;
    }

    /**
     * Gets the numerical identifier of the ring.
     *
     * @return The numerical identifier of the ring.
     */
    public int getNum() {
        return num;
    }
}
