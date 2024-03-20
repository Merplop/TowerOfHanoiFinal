package com.mirohaap.towerofhanoitutor;

import javafx.animation.TranslateTransition;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

/**
 * Manages animations for the Tower of Hanoi tutor application. This class keeps track of all
 * ongoing animations and notifies listeners when all animations have completed. It uses the
 * Singleton design pattern to ensure that there is only one instance of this class throughout
 * the application.
 */
public class AnimationRepository {
    private static AnimationRepository _instance;
    private ArrayList<TranslateTransition> animations;
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);

    /**
     * Private constructor to prevent instantiation from outside this class. Initializes the
     * animations list.
     */
    private AnimationRepository() {
        animations = new ArrayList<>();
    }

    /**
     * Adds a PropertyChangeListener that will be notified of changes to the animation list,
     * specifically when all animations have completed.
     *
     * @param listener The PropertyChangeListener to add.
     */
    public void addListener(PropertyChangeListener listener) {
        changes.addPropertyChangeListener(listener);
    }

    /**
     * Checks if there are any animations currently running.
     *
     * @return true if there are running animations, false otherwise.
     */
    public boolean animationsRunning() {
        return !animations.isEmpty();
    }

    /**
     * Removes an animation from the list of running animations. If removing this animation
     * results in no more running animations, it will notify all registered listeners that all
     * animations have completed.
     *
     * @param animation The animation to remove.
     */
    public void remove(TranslateTransition animation) {
        animations.remove(animation);
        System.out.println("removed");
        if (animations.isEmpty()) {
            System.out.println("firing");
            changes.firePropertyChange("all_animations_complete", null, null);
        }
    }

    /**
     * Adds an animation to the list of running animations.
     *
     * @param animation The animation to add.
     */
    public void add(TranslateTransition animation) {
        System.out.println("added animation");
        animations.add(animation);
    }

    /**
     * Provides access to the Singleton instance of the AnimationRepository, creating it if necessary.
     *
     * @return The singleton instance of AnimationRepository.
     */
    public static AnimationRepository getInstance() {
        if (_instance == null) {
            _instance = new AnimationRepository();
        }
        return _instance;
    }
}
