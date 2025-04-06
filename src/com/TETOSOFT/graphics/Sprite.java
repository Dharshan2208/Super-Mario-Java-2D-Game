package com.TETOSOFT.graphics;

import java.awt.Image;

/**
 * The Sprite class represents a movable game object with animation.
 * It handles position, velocity, and maintains an animation sequence.
 * This class provides the foundation for animated game characters and objects.
 */
public class Sprite {

    /** The animation sequence for this sprite */
    protected Animation anim;

    /** X-coordinate position of the sprite */
    private float x;

    /** Y-coordinate position of the sprite */
    private float y;

    /** Horizontal velocity (pixels per millisecond) */
    private float dx;

    /** Vertical velocity (pixels per millisecond) */
    private float dy;

    /**
     * Creates a new Sprite with the specified animation.
     *
     * @param anim The animation sequence to use for this sprite
     */
    public Sprite(Animation anim) {
        this.anim = anim;
    }

    /**
     * Updates the sprite's position and animation based on elapsed time.
     * Position is updated according to the sprite's velocity.
     *
     * @param elapsedTime Time elapsed since the last update (in milliseconds)
     */
    public void update(long elapsedTime) {
        // Update position based on velocity and elapsed time
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        // Update the animation frame
        anim.update(elapsedTime);
    }

    /**
     * Gets the sprite's X-coordinate.
     *
     * @return The current X position
     */
    public float getX() {
        return x;
    }

    /**
     * Gets the sprite's Y-coordinate.
     *
     * @return The current Y position
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the sprite's X-coordinate.
     *
     * @param x The new X position
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Sets the sprite's Y-coordinate.
     *
     * @param y The new Y position
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets the width of the sprite's current image.
     *
     * @return The width in pixels
     */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
     * Gets the height of the sprite's current image.
     *
     * @return The height in pixels
     */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
     * Gets the sprite's horizontal velocity.
     *
     * @return The horizontal velocity (pixels per millisecond)
     */
    public float getVelocityX() {
        return dx;
    }

    /**
     * Gets the sprite's vertical velocity.
     *
     * @return The vertical velocity (pixels per millisecond)
     */
    public float getVelocityY() {
        return dy;
    }

    /**
     * Sets the sprite's horizontal velocity.
     *
     * @param dx The new horizontal velocity (pixels per millisecond)
     */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
     * Sets the sprite's vertical velocity.
     *
     * @param dy The new vertical velocity (pixels per millisecond)
     */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
     * Gets the current image of the sprite's animation.
     *
     * @return The current image frame
     */
    public Image getImage() {
        return anim.getImage();
    }

    /**
     * Creates and returns a copy of this sprite.
     * Note: This creates a new Sprite instance with the same animation,
     * but position and velocity are reset.
     *
     * @return A clone of this sprite
     */
    public Object clone() {
        return new Sprite(anim);
    }
}