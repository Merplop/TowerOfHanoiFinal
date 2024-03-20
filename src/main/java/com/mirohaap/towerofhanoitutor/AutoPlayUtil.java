package com.mirohaap.towerofhanoitutor;

import javafx.animation.Animation;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Facilitates the automatic playing of the Tower of Hanoi game by executing a series of moves
 * at a fixed interval. It manages the scheduling of moves and coordinates with other utility
 * classes to animate these moves in the UI.
 */
public class AutoPlayUtil {
    private DragDropUtil dragDropUtil;
    private ScheduledExecutorService exec;
    private MutableBoolean reenable;

    /**
     * Constructs an AutoPlayUtil instance with a specified DragDropUtil.
     *
     * @param dragDropUtil The DragDropUtil instance for managing drag and drop operations and animations.
     */
    public AutoPlayUtil(DragDropUtil dragDropUtil) {
        this.dragDropUtil = dragDropUtil;
        reenable = new MutableBoolean(false);
    }

    /**
     * Begins the automatic playing of the game, scheduling moves at the specified interval.
     *
     * @param interval The interval in milliseconds between each move.
     */
    public void beginPlaying(int interval) {
        dragDropUtil.disableUserInput();

        exec = Executors.newSingleThreadScheduledExecutor();
        Runnable makeNextMove = () -> {
            Move next = Tutor.getInstance().getNextMove();
            next.setValid(true);
            Repository.getInstance().applyMove(next);
            Platform.runLater(() -> dragDropUtil.animateMove(next, interval * 0.9, reenable));
        };

        exec.scheduleAtFixedRate(makeNextMove, 0, interval, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops the automatic playing of the game and ensures that user input is re-enabled once
     * all animations have completed.
     */
    public void stopPlaying() {
        exec.shutdown();
        if (AnimationRepository.getInstance().animationsRunning()) {
            reenable.setTrue();
        } else {
            dragDropUtil.enableUserInput();
        }
    }
}
