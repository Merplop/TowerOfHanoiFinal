package com.mirohaap.towerofhanoitutor;

/**
 * Defines a snapping range around a point (original X and Y coordinates) to determine
 * if an object is close enough to be considered "within range" for snapping purposes.
 * It also keeps track of a tower and an optional owner ring.
 */
public class SnapRange {
    private final double ogX, ogY; // Original X and Y coordinates.
    private double maxX, maxY, minX, minY; // Max and min X and Y for the snap range.
    private static final int DEFAULT_SNAP_RANGE = 100; // Default snap range value.
    private Ring owner; // The ring that owns this snap range, if any.
    private int tower; // The tower associated with this snap range.

    /**
     * Constructor for SnapRange specifying the original X and Y coordinates, snap range, and tower.
     *
     * @param x     The original X-coordinate.
     * @param y     The original Y-coordinate.
     * @param range The snap range distance.
     * @param tower The tower index associated with this snap range.
     */
    public SnapRange(double x, double y, double range, int tower) {
        this.ogX = x;
        this.ogY = y;
        setRange(range);
        this.owner = null;
        this.tower = tower;
    }

    /**
     * Constructs a SnapRange and associates it with a specific Ring and tower.
     *
     * @param x     The original X-coordinate.
     * @param y     The original Y-coordinate.
     * @param range The snap range distance.
     * @param owner The ring that owns this snap range.
     */
    public SnapRange(double x, double y, double range, Ring owner) {
        this(x, y, range, Repository.getInstance().getTower(owner.getNum()));
        this.owner = owner;
    }

    /**
     * Checks if the specified coordinates are within this snap range.
     *
     * @param x The X-coordinate to check.
     * @param y The Y-coordinate to check.
     * @return True if the coordinates are within the snap range, false otherwise.
     */
    public boolean inRange(double x, double y) {
        return x < maxX && x > minX && y < maxY && y > minY;
    }

    /**
     * Sets the boundaries of the snap range based on the original coordinates and the provided range.
     *
     * @param range The distance from the original point that defines the snap range.
     */
    public void setRange(double range) {
        maxX = ogX + range;
        maxY = ogY + range;
        minX = ogX - range;
        minY = ogY - range;
    }

    // Getters for various properties of the SnapRange.

    public double getOgX() {
        return ogX;
    }

    public double getOgY() {
        return ogY;
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public boolean isOwner(Ring ring) {
        return ring.equals(owner);
    }

    public Ring getOwner() {
        return owner;
    }

    public int getTower() {
        return tower;
    }
}
