package com.mirohaap.towerofhanoitutor;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableBoolean;

/**
 * Utility class for handling drag and drop functionality in the Tower of Hanoi game.
 */
public class DragDropUtil {
    private List<Ring> rings;
    private AnchorPane gamePane;
    private List<SnapRange> tops;
    private Rectangle projection;
    private boolean hasWon;
    private static final double TOWER_BOTTOM_Y = 360, DEFAULT_RANGE = 50;
    public static final double[] TOWER_CENTERS = {128.0, 364.0, 600.0};
    private static final SnapRange TOWER_1_BOTTOM = new SnapRange(TOWER_CENTERS[0], TOWER_BOTTOM_Y, DEFAULT_RANGE, 1), TOWER_2_BOTTOM = new SnapRange(TOWER_CENTERS[1], TOWER_BOTTOM_Y, DEFAULT_RANGE, 2), TOWER_3_BOTTOM = new SnapRange(TOWER_CENTERS[2], TOWER_BOTTOM_Y, DEFAULT_RANGE, 3);
    private double startX, startY, offsetX, offsetY;

    /**
     * Constructs a DragDropUtil object.
     *
     * @param gamePane the AnchorPane representing the game pane
     * @param rings    the list of rings in the game
     */
    public DragDropUtil(AnchorPane gamePane, List<Ring> rings) {
        this.gamePane = gamePane;
        this.rings = rings;
        for (Ring ring : rings) {
            makeRingDraggable(ring);
        }
        // This assumes the game starts with all rings on tower 1
        tops = new ArrayList<>();
        refreshTops();
    }

    /**
     * Makes a ring draggable.
     *
     * @param ring the ring to make draggable
     */
    private void makeRingDraggable(Ring ring) {
        StackPane ringPane = ring.getVisualRing();
        ringPane.setOnMousePressed(e -> {
            if (!Repository.getInstance().isTop(ring.getNum())) {
                return;
            }

            ringPane.setCursor(Cursor.CLOSED_HAND);
            startX = ringPane.getLayoutX();
            startY = ringPane.getLayoutY();
            offsetX = e.getSceneX() - ringPane.getLayoutX();
            offsetY = e.getSceneY() - ringPane.getLayoutY();
        });

        ringPane.setOnMouseDragged(e -> {
            if (!Repository.getInstance().isTop(ring.getNum())) {
                return;
            }

            ringPane.setLayoutX(e.getSceneX() - offsetX);
            ringPane.setLayoutY(e.getSceneY() - offsetY);

            SnapRange inRange = checkSnapRanges(ring);
            clearProjection();
            if (inRange != null) {
                if (!inRange.hasOwner() || inRange.getOwner().getNum() > ring.getNum()) { // Checks if ring is valid to be placed
                    projectRect(ringPane, inRange, Color.BLACK);
                } else {
                    projectRect(ringPane, inRange, Color.RED);
                }
            }
            ringPane.setViewOrder(-1);
        });

        ringPane.setOnMouseReleased(e -> {
            if (!Repository.getInstance().isTop(ring.getNum())) {
                return;
            }

            clearProjection();
            SnapRange inRange = checkSnapRanges(ring);
            Move made = null;
            if (inRange != null && (!inRange.hasOwner() || inRange.getOwner().getNum() > ring.getNum())) {
                ringPane.setCursor(Cursor.DEFAULT);
                made = new Move(ring.getNum(), Repository.getInstance().getTower(ring.getNum()), inRange.getTower());
                if (Tutor.getInstance().validateMove(made)) {
                    ringPane.setLayoutX(inRange.getOgX() - (ringPane.getWidth() / 2));
                    ringPane.setLayoutY(inRange.getOgY() - ringPane.getHeight() + 1);

                    refreshCursors(made);
                }

                Repository.getInstance().applyMove(made);

                if (Repository.getInstance().checkWin()) {
                    System.out.println("Winner!");
                    disableUserInput();
                    hasWon = true;
                }
            }

            if (made == null || !made.isValid()) {
                if (inRange != null) {
                    SoundPlayer.getInstance().playWrong();
                }
                ringPane.setCursor(Cursor.OPEN_HAND);
                ringPane.setLayoutX(startX);
                ringPane.setLayoutY(startY);
            } else {
                SoundPlayer.getInstance().playPlace();
            }

            ringPane.setViewOrder(0);
            refreshTops();
        });
    }

    /**
     * Refreshes the cursors for all towers.
     */
    private void refreshCursors() {
        refreshCursors(Repository.getInstance().getTowerByIndex(0));
        refreshCursors(Repository.getInstance().getTowerByIndex(1));
        refreshCursors(Repository.getInstance().getTowerByIndex(2));
    }

    /**
     * Refreshes the cursors for the towers involved in a move.
     *
     * @param move the move that was made
     */
    private void refreshCursors(Move move) {
        refreshCursors(Repository.getInstance().getTowerByIndex(move.getTo() - 1));
        refreshCursors(Repository.getInstance().getTowerByIndex(move.getFrom() - 1));
    }

    /**
     * Refreshes the cursors for a specific tower.
     *
     * @param toRefresh the list of rings on the tower to refresh
     */
    private void refreshCursors(List<Integer> toRefresh) {
        for (Integer i : toRefresh) {
            Ring ring = rings.get(i - 1);
            if (!ring.equals(toRefresh.getLast())) {
                ring.getVisualRing().setCursor(Cursor.DEFAULT);
            }
        }
    }

    /**
     * Refreshes the tops of the towers.
     */
    private void refreshTops() {
        tops.clear();
        List<Integer> intTops = Repository.getInstance().getTops();
        for (int i = 0; i < 3; i++) {
            if (intTops.get(i) == -1) {
                switch (i) {
                    case 0:
                        tops.add(TOWER_1_BOTTOM);
                        break;
                    case 1:
                        tops.add(TOWER_2_BOTTOM);
                        break;
                    case 2:
                        tops.add(TOWER_3_BOTTOM);
                        break;
                    default:
                        break;
                }
            } else {
                Ring top = rings.get(intTops.get(i) - 1);
                top.getVisualRing().setCursor(Cursor.OPEN_HAND);
                tops.add(new SnapRange(top.getVisualRing().getLayoutX() + (top.getVisualRing().getWidth() / 2), top.getVisualRing().getLayoutY(), DEFAULT_RANGE, top));
            }
        }
    }

    /**
     * Checks if a ring is within the snap range of any tower.
     *
     * @param ring the ring to check
     * @return the SnapRange of the tower the ring is within, or null if not within any range
     */
    private SnapRange checkSnapRanges(Ring ring) {
        for (SnapRange top : tops) {
            if (!top.isOwner(ring) && top.inRange(ring.getVisualRing().getLayoutX() + (ring.getVisualRing().getWidth() / 2), ring.getVisualRing().getLayoutY() + (ring.getVisualRing().getHeight() / 2))) {
                return top;
            }
        }
        return null;
    }

    /**
     * Clears the projection rectangle.
     */
    private void clearProjection() {
        if (projection != null) {
            projection.setVisible(false);
            gamePane.getChildren().remove(projection);
        }
        projection = null;
    }

    /**
     * Projects a rectangle to indicate where a ring will be placed.
     *
     * @param example the StackPane representing the ring
     * @param sr      the SnapRange where the ring will be placed
     * @param color   the color of the projected rectangle
     */
    public void projectRect(StackPane example, SnapRange sr, Color color) {
        projection = new Rectangle(sr.getOgX() - (example.getWidth() / 2), sr.getOgY() - example.getHeight(), example.getWidth(), example.getHeight());
        projection.setFill(Color.TRANSPARENT);
        projection.setStroke(color);
        projection.setStrokeWidth(3);
        projection.setVisible(true);
        gamePane.getChildren().add(projection);
    }

    /**
     * Animates a move.
     *
     * @param move     the move to animate
     * @param interval the duration of the animation in milliseconds
     * @param reenable a mutable boolean indicating whether to re-enable user input after the animation
     * @return the TranslateTransition representing the animation
     */
    public TranslateTransition animateMove(Move move, double interval, MutableBoolean reenable) {
        Ring moving = rings.get(move.getN() - 1);
        moving.getVisualRing().setViewOrder(-1.0);
        double destinationX = TOWER_CENTERS[move.getTo() - 1] - (moving.getVisualRing().getWidth() / 2);
        double destinationY = TOWER_BOTTOM_Y - 1 - (Repository.getInstance().getTowerByIndex(move.getTo() - 1).size() * 29);
        System.out.println(destinationY);

        TranslateTransition transition = new TranslateTransition(Duration.millis(interval), moving.getVisualRing());
        transition.setToX(destinationX - moving.getVisualRing().getLayoutX());
        transition.setToY(destinationY - moving.getVisualRing().getLayoutY());

        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("finished");
                SoundPlayer.getInstance().playPlace();
                moving.getVisualRing().setTranslateX(0);
                moving.getVisualRing().setTranslateY(0);
                moving.getVisualRing().setLayoutX(destinationX);
                moving.getVisualRing().setLayoutY(destinationY);
                moving.getVisualRing().setViewOrder(0);
                AnimationRepository.getInstance().remove(transition);
                if (reenable.isTrue()) {
                    System.out.println("enabling");
                    enableUserInput();
                }
            }
        });

        AnimationRepository.getInstance().add(transition);
        transition.play();
        return transition;
    }

    /**
     * Allows or disallows user input.
     *
     * @param allow true to allow user input, false to disallow
     */
    public void allowUserInput(boolean allow) {
        if (allow) {
            enableUserInput();
        } else {
            disableUserInput();
        }
    }

    /**
     * Disables user input.
     */
    public void disableUserInput() {
        gamePane.setDisable(true);
    }

    /**
     * Enables user input.
     */
    public void enableUserInput() {
        refreshCursors();
        refreshTops();
        gamePane.setDisable(false);
    }

    /**
     * Checks if the game has been won.
     *
     * @return true if the game has been won, false otherwise
     */
    public boolean hasWon() {
        return hasWon;
    }
}