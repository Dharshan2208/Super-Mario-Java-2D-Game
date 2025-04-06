package com.TETOSOFT.graphics;

import java.awt.Image;
import java.util.ArrayList;

/**
 * The Animation class manages a sequence of images (frames) that can be
 * displayed in succession to create an animation effect.
 * It handles timing, frame advancement, and provides methods to control
 * the animation sequence.
 */
public class Animation {
    /** List to store animation frames */
    private ArrayList frames;

    /** Index of the current frame being displayed */
    private int currFrameIndex;

    /** Time elapsed in the current animation cycle */
    private long animTime;

    /** Total duration of all frames in the animation */
    private long totalDuration;

    /**
     * Default constructor that creates an empty animation.
     */
    public Animation() {
        this(new ArrayList(), 0);
    }

    /**
     * Private constructor used for cloning animations.
     *
     * @param frames        List of animation frames
     * @param totalDuration Total duration of the animation in milliseconds
     */
    private Animation(ArrayList frames, long totalDuration) {
        this.frames = frames;
        this.totalDuration = totalDuration;
        start();
    }

    /**
     * Creates and returns a copy of this animation.
     *
     * @return A clone of this Animation object
     */
    public Object clone() {
        return new Animation(frames, totalDuration);
    }

    /**
     * Adds a new frame to the animation.
     *
     * @param image    The image for the new frame
     * @param duration How long this frame should be displayed (in milliseconds)
     */
    public synchronized void addFrame(Image image, long duration) {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }

    /**
     * Resets the animation to the beginning.
     */
    public synchronized void start() {
        animTime = 0;
        currFrameIndex = 0;
    }

    /**
     * Updates the animation based on elapsed time.
     * Advances frames as needed based on their durations.
     *
     * @param elapsedTime Time elapsed since the last update (in milliseconds)
     */
    public synchronized void update(long elapsedTime) {
        if (frames.size() > 1) {
            animTime += elapsedTime;

            // If we've gone through the whole animation, loop back to start
            if (animTime >= totalDuration) {
                animTime = animTime % totalDuration;
                currFrameIndex = 0;
            }

            // Advance frames until we find the correct one for the current time
            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;
            }
        }
    }

    /**
     * Gets the current frame's image.
     *
     * @return The image of the current animation frame, or null if no frames exist
     */
    public synchronized Image getImage() {
        if (frames.size() == 0) {
            return null;
        } else {
            return getFrame(currFrameIndex).image;
        }
    }

    /**
     * Helper method to get a frame at a specific index.
     *
     * @param i Index of the frame to retrieve
     * @return The AnimFrame at the specified index
     */
    private AnimFrame getFrame(int i) {
        return (AnimFrame) frames.get(i);
    }

    /**
     * Inner class that represents a single frame in the animation.
     * Each frame has an image and the time at which it ends.
     */
    private class AnimFrame {
        /** The image for this frame */
        Image image;

        /**
         * The time (in milliseconds) at which this frame ends in the animation sequence
         */
        long endTime;

        /**
         * Constructs a new animation frame.
         *
         * @param image   The image for this frame
         * @param endTime The time at which this frame ends
         */
        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }
}