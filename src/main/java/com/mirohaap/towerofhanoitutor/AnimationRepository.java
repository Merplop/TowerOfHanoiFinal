package com.mirohaap.towerofhanoitutor;

import javafx.animation.TranslateTransition;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Stack;

public class AnimationRepository {
    private static AnimationRepository _instance;
    private ArrayList<TranslateTransition> animations;
    private PropertyChangeSupport changes = new PropertyChangeSupport(this);

    private AnimationRepository(){
        animations = new ArrayList<>();
    }

    public void addListener(PropertyChangeListener listener){
        changes.addPropertyChangeListener(listener);
    }

    public boolean animationsRunning(){
        return !animations.isEmpty();
    }

    public void remove(TranslateTransition animation){
        animations.remove(animation);
        System.out.println("removed");
        if(animations.isEmpty()){
            System.out.println("firing");
            changes.firePropertyChange("all_animations_complete", null, null);
        }

    }

    public void add(TranslateTransition animation){
        System.out.println("added animation");
        animations.add(animation);
    }

    public static AnimationRepository getInstance(){
        if(_instance == null){
            _instance = new AnimationRepository();
        }
        return _instance;
    }

}




