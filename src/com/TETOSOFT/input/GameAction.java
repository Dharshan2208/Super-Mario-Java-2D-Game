package com.TETOSOFT.input;

/**
 * The GameAction class represents a specific game action or input command
 * (such as jump, move left, fire, etc.) that can be triggered by various input
 * devices like keyboard, mouse, or gamepad. It manages the state of the action
 * and provides methods to check its status.
 */
public class GameAction {
    /**
     * Normal behavior mode: reports the action as pressed until it is released.
     * Each getAmount() call will return the amount while pressed.
     */
    public static final int NORMAL = 0;

    /**
     * Alternative behavior mode: reports the action as pressed only once
     * until the action is released and pressed again.
     * Useful for actions like jumping that should not repeat while a button is
     * held.
     */
    public static final int DETECT_INITAL_PRESS_ONLY = 1;

    /** Indicates the action is in the released state */
    private static final int STATE_RELEASED = 0;

    /** Indicates the action is in the pressed state */
    private static final int STATE_PRESSED = 1;

    /** Indicates the action is waiting for release after initial detection */
    private static final int STATE_WAITING_FOR_RELEASE = 2;

    /** The name of this action for identification purposes */
    private String name;

    /** The behavior mode (NORMAL or DETECT_INITAL_PRESS_ONLY) */
    private int behavior;

    /**
     * The amount or intensity of this action (e.g., how far a button is pressed)
     */
    private int amount;

    /** The current state of this action */
    private int state;

    /**
     * Creates a new GameAction with the specified name and default NORMAL behavior.
     *
     * @param name The name of this action
     */
    public GameAction(String name) {
        this(name, NORMAL);
    }

    /**
     * Creates a new GameAction with the specified name and behavior.
     *
     * @param name     The name of this action
     * @param behavior The behavior mode (NORMAL or DETECT_INITAL_PRESS_ONLY)
     */
    public GameAction(String name, int behavior) {
        this.name = name;
        this.behavior = behavior;
        reset();
    }

    /**
     * Gets the name of this action.
     *
     * @return The name of this action
     */
    public String getName() {
        return name;
    }

    /**
     * Resets this action to its initial state (released with amount 0).
     */
    public void reset() {
        state = STATE_RELEASED;
        amount = 0;
    }

    /**
     * Performs a quick tap of this action (press followed by immediate release).
     */
    public synchronized void tap() {
        press();
        release();
    }

    /**
     * Marks this action as pressed with a default amount of 1.
     */
    public synchronized void press() {
        press(1);
    }

    /**
     * Marks this action as pressed with the specified amount.
     * If the action is waiting for release, this method has no effect.
     *
     * @param amount The amount or intensity of the press
     */
    public synchronized void press(int amount) {
        if (state != STATE_WAITING_FOR_RELEASE) {
            this.amount += amount;
            state = STATE_PRESSED;
        }
    }

    /**
     * Marks this action as released.
     */
    public synchronized void release() {
        state = STATE_RELEASED;
    }

    /**
     * Checks if this action is currently pressed.
     *
     * @return true if the action is pressed, false otherwise
     */
    public synchronized boolean isPressed() {
        return (getAmount() != 0);
    }

    /**
     * Gets the amount or intensity of this action. Depending on the behavior mode,
     * this method may reset the amount or change the state of the action.
     *
     * In NORMAL mode, the amount remains until the action is released.
     * In DETECT_INITAL_PRESS_ONLY mode, the amount is returned once, then reset to
     * 0.
     *
     * @return The current amount of this action
     */
    public synchronized int getAmount() {
        int retVal = amount;
        if (retVal != 0) {
            if (state == STATE_RELEASED) {
                // If the state is released, reset amount
                amount = 0;
            } else if (behavior == DETECT_INITAL_PRESS_ONLY) {
                // If we're only detecting initial press, change state
                // and reset amount until the action is released and pressed again
                state = STATE_WAITING_FOR_RELEASE;
                amount = 0;
            }
        }
        return retVal;
    }
}