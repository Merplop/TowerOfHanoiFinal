package com.mirohaap.towerofhanoitutor;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Singleton class for playing sound effects in the Tower of Hanoi tutor application.
 * This class manages a collection of "place" sounds and a single "wrong" sound,
 * allowing for audio feedback during game play.
 */
public class SoundPlayer {
    private static SoundPlayer instance;
    private Clip[] placeClips; // Clips for place sounds.
    private Clip wrongClip; // Clip for wrong action sound.
    private int currentPlaceIndex; // Index for the next place clip to play.

    /**
     * Private constructor to prevent instantiation outside of getInstance method.
     * Loads the sound files into memory for quick access.
     */
    private SoundPlayer() {
        // Load place sound files
        placeClips = new Clip[4];
        for (int i = 0; i < placeClips.length; i++) {
            try {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("place" + (i + 1) + ".wav"));
                placeClips[i] = AudioSystem.getClip();
                placeClips[i].open(audioInputStream);
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }

        // Load wrong sound file
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("wrong.wav"));
            wrongClip = AudioSystem.getClip();
            wrongClip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        currentPlaceIndex = 0;
    }

    /**
     * Gets the singleton instance of the SoundPlayer.
     *
     * @return The singleton instance of SoundPlayer.
     */
    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }

    /**
     * Plays one of the "place" sounds. Cycles through a set of sounds to provide variety.
     */
    public void playPlace() {
        Clip clip = placeClips[currentPlaceIndex];
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0); // Rewind to the beginning of the clip.
        clip.start(); // Play the sound.
        currentPlaceIndex = (currentPlaceIndex + 1) % placeClips.length; // Move to the next clip for the next call.
    }

    /**
     * Plays the "wrong" action sound.
     */
    public void playWrong() {
        if (wrongClip.isRunning()) {
            wrongClip.stop();
        }
        wrongClip.setFramePosition(0); // Rewind to the beginning of the clip.
        wrongClip.start(); // Play the sound.
    }

    // Additional methods can be added here for playing other sound effects as needed.
}
