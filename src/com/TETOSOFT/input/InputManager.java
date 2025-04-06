package com.TETOSOFT.input;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 * The InputManager class handles all user input for a game, including keyboard
 * and mouse interactions. It maps physical input events to logical game
 * actions,
 * supports both absolute and relative mouse movement modes, and handles event
 * processing.
 *
 * This class implements multiple listener interfaces to capture different types
 * of input events.
 */
public class InputManager implements KeyListener, MouseListener,
        MouseMotionListener, MouseWheelListener {
    /**
     * An invisible cursor that can be used when in relative mouse mode
     * to hide the system cursor.
     */
    public static final Cursor INVISIBLE_CURSOR = Toolkit.getDefaultToolkit().createCustomCursor(
            Toolkit.getDefaultToolkit().getImage(""),
            new Point(0, 0),
            "invisible");

    // Mouse action codes
    /** Code for mouse movement to the left */
    public static final int MOUSE_MOVE_LEFT = 0;
    /** Code for mouse movement to the right */
    public static final int MOUSE_MOVE_RIGHT = 1;
    /** Code for mouse movement upward */
    public static final int MOUSE_MOVE_UP = 2;
    /** Code for mouse movement downward */
    public static final int MOUSE_MOVE_DOWN = 3;
    /** Code for mouse wheel rotation upward */
    public static final int MOUSE_WHEEL_UP = 4;
    /** Code for mouse wheel rotation downward */
    public static final int MOUSE_WHEEL_DOWN = 5;
    /** Code for left mouse button (button 1) */
    public static final int MOUSE_BUTTON_1 = 6;
    /** Code for middle mouse button (button 2) */
    public static final int MOUSE_BUTTON_2 = 7;
    /** Code for right mouse button (button 3) */
    public static final int MOUSE_BUTTON_3 = 8;

    /** Total number of defined mouse action codes */
    private static final int NUM_MOUSE_CODES = 9;

    /** Maximum number of key codes to support */
    private static final int NUM_KEY_CODES = 600;

    /** Maps key codes to game actions */
    private GameAction[] keyActions = new GameAction[NUM_KEY_CODES];

    /** Maps mouse codes to game actions */
    private GameAction[] mouseActions = new GameAction[NUM_MOUSE_CODES];

    /** Current mouse position */
    private Point mouseLocation;

    /** Center location used for relative mouse mode */
    private Point centerLocation;

    /** The component receiving input events */
    private Component comp;

    /** Robot for programmatically moving the mouse cursor */
    private Robot robot;

    /** Flag indicating if the mouse is currently being recentered */
    private boolean isRecentering;

    /**
     * Creates a new InputManager for the specified component.
     *
     * @param comp The component to receive input events
     */
    public InputManager(Component comp) {
        this.comp = comp;
        mouseLocation = new Point();
        centerLocation = new Point();

        // Register this input manager as a listener for all input events
        comp.addKeyListener(this);
        comp.addMouseListener(this);
        comp.addMouseMotionListener(this);
        comp.addMouseWheelListener(this);

        // Disable focus traversal keys (Tab, Shift+Tab, etc.)
        comp.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Sets the cursor for the component.
     *
     * @param cursor The cursor to use
     */
    public void setCursor(Cursor cursor) {
        comp.setCursor(cursor);
    }

    /**
     * Enables or disables relative mouse mode. In relative mode,
     * the mouse is continuously recentered and only the relative
     * movement is reported.
     *
     * @param mode true to enable relative mouse mode, false for absolute mode
     */
    public void setRelativeMouseMode(boolean mode) {
        if (mode == isRelativeMouseMode()) {
            return;
        }

        if (mode) {
            try {
                // Create robot for automatic mouse repositioning
                robot = new Robot();
                recenterMouse();
            } catch (AWTException ex) {
                // Robot not available, cannot use relative mode
                robot = null;
            }
        } else {
            robot = null;
        }
    }

    /**
     * Checks if relative mouse mode is currently enabled.
     *
     * @return true if relative mouse mode is enabled, false otherwise
     */
    public boolean isRelativeMouseMode() {
        return (robot != null);
    }

    /**
     * Maps a game action to a specific key.
     *
     * @param gameAction The game action to map
     * @param keyCode    The key code to map to (from KeyEvent constants)
     */
    public void mapToKey(GameAction gameAction, int keyCode) {
        keyActions[keyCode] = gameAction;
    }

    /**
     * Maps a game action to a specific mouse input.
     *
     * @param gameAction The game action to map
     * @param mouseCode  The mouse code to map to (MOUSE_* constants)
     */
    public void mapToMouse(GameAction gameAction,
            int mouseCode) {
        mouseActions[mouseCode] = gameAction;
    }

    /**
     * Removes all mappings for the specified game action.
     *
     * @param gameAction The game action to clear mappings for
     */
    public void clearMap(GameAction gameAction) {
        // Clear key mappings
        for (int i = 0; i < keyActions.length; i++) {
            if (keyActions[i] == gameAction) {
                keyActions[i] = null;
            }
        }

        // Clear mouse mappings
        for (int i = 0; i < mouseActions.length; i++) {
            if (mouseActions[i] == gameAction) {
                mouseActions[i] = null;
            }
        }

        // Reset the game action
        gameAction.reset();
    }

    /**
     * Gets a list of all input mappings for the specified game action.
     *
     * @param gameCode The game action to get mappings for
     * @return A list of strings representing the mappings
     */
    public List getMaps(GameAction gameCode) {
        ArrayList list = new ArrayList();

        // Add key mappings
        for (int i = 0; i < keyActions.length; i++) {
            if (keyActions[i] == gameCode) {
                list.add(getKeyName(i));
            }
        }

        // Add mouse mappings
        for (int i = 0; i < mouseActions.length; i++) {
            if (mouseActions[i] == gameCode) {
                list.add(getMouseName(i));
            }
        }
        return list;
    }

    /**
     * Resets all game actions to their initial state.
     * Useful when changing game states or screens.
     */
    public void resetAllGameActions() {
        // Reset key actions
        for (int i = 0; i < keyActions.length; i++) {
            if (keyActions[i] != null) {
                keyActions[i].reset();
            }
        }

        // Reset mouse actions
        for (int i = 0; i < mouseActions.length; i++) {
            if (mouseActions[i] != null) {
                mouseActions[i].reset();
            }
        }
    }

    /**
     * Gets a human-readable name for the specified key code.
     *
     * @param keyCode The key code
     * @return A string representation of the key
     */
    public static String getKeyName(int keyCode) {
        return KeyEvent.getKeyText(keyCode);
    }

    /**
     * Gets a human-readable name for the specified mouse code.
     *
     * @param mouseCode The mouse code (MOUSE_* constants)
     * @return A string representation of the mouse action
     */
    public static String getMouseName(int mouseCode) {
        switch (mouseCode) {
            case MOUSE_MOVE_LEFT:
                return "Mouse Left";
            case MOUSE_MOVE_RIGHT:
                return "Mouse Right";
            case MOUSE_MOVE_UP:
                return "Mouse Up";
            case MOUSE_MOVE_DOWN:
                return "Mouse Down";
            case MOUSE_WHEEL_UP:
                return "Mouse Wheel Up";
            case MOUSE_WHEEL_DOWN:
                return "Mouse Wheel Down";
            case MOUSE_BUTTON_1:
                return "Mouse Button 1";
            case MOUSE_BUTTON_2:
                return "Mouse Button 2";
            case MOUSE_BUTTON_3:
                return "Mouse Button 3";
            default:
                return "Unknown mouse code " + mouseCode;
        }
    }

    /**
     * Gets the current X coordinate of the mouse.
     *
     * @return The mouse X coordinate
     */
    public int getMouseX() {
        return mouseLocation.x;
    }

    /**
     * Gets the current Y coordinate of the mouse.
     *
     * @return The mouse Y coordinate
     */
    public int getMouseY() {
        return mouseLocation.y;
    }

    /**
     * Recenters the mouse to the middle of the component.
     * Used in relative mouse mode to prevent the cursor from
     * leaving the component area.
     */
    private synchronized void recenterMouse() {
        if (robot != null && comp.isShowing()) {
            // Calculate center of component
            centerLocation.x = comp.getWidth() / 2;
            centerLocation.y = comp.getHeight() / 2;

            // Convert to screen coordinates
            SwingUtilities.convertPointToScreen(centerLocation,
                    comp);

            // Move mouse to center
            isRecentering = true;
            robot.mouseMove(centerLocation.x, centerLocation.y);
        }
    }

    /**
     * Gets the game action associated with a key event.
     *
     * @param e The key event
     * @return The associated game action, or null if none
     */
    private GameAction getKeyAction(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode < keyActions.length) {
            return keyActions[keyCode];
        } else {
            return null;
        }
    }

    /**
     * Converts a mouse event button to our internal mouse button code.
     *
     * @param e The mouse event
     * @return The corresponding MOUSE_BUTTON_* code, or -1 if invalid
     */
    public static int getMouseButtonCode(MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                return MOUSE_BUTTON_1;
            case MouseEvent.BUTTON2:
                return MOUSE_BUTTON_2;
            case MouseEvent.BUTTON3:
                return MOUSE_BUTTON_3;
            default:
                return -1;
        }
    }

    /**
     * Gets the game action associated with a mouse button event.
     *
     * @param e The mouse event
     * @return The associated game action, or null if none
     */
    private GameAction getMouseButtonAction(MouseEvent e) {
        int mouseCode = getMouseButtonCode(e);
        if (mouseCode != -1) {
            return mouseActions[mouseCode];
        } else {
            return null;
        }
    }

    /**
     * Handles key press events by triggering the corresponding game action.
     * Implements KeyListener interface.
     *
     * @param e The key event
     */
    public void keyPressed(KeyEvent e) {
        GameAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.press();
        }
        // make sure the key isn't processed for anything else
        e.consume();
    }

    /**
     * Handles key release events by releasing the corresponding game action.
     * Implements KeyListener interface.
     *
     * @param e The key event
     */
    public void keyReleased(KeyEvent e) {
        GameAction gameAction = getKeyAction(e);
        if (gameAction != null) {
            gameAction.release();
        }
        // make sure the key isn't processed for anything else
        e.consume();
    }

    /**
     * Handles key typed events by consuming them.
     * Implements KeyListener interface.
     *
     * @param e The key event
     */
    public void keyTyped(KeyEvent e) {
        // make sure the key isn't processed for anything else
        e.consume();
    }

    /**
     * Handles mouse press events by triggering the corresponding game action.
     * Implements MouseListener interface.
     *
     * @param e The mouse event
     */
    public void mousePressed(MouseEvent e) {
        GameAction gameAction = getMouseButtonAction(e);
        if (gameAction != null) {
            gameAction.press();
        }
    }

    /**
     * Handles mouse release events by releasing the corresponding game action.
     * Implements MouseListener interface.
     *
     * @param e The mouse event
     */
    public void mouseReleased(MouseEvent e) {
        GameAction gameAction = getMouseButtonAction(e);
        if (gameAction != null) {
            gameAction.release();
        }
    }

    /**
     * Handles mouse click events (not used).
     * Implements MouseListener interface.
     *
     * @param e The mouse event
     */
    public void mouseClicked(MouseEvent e) {
        // do nothing
    }

    /**
     * Handles mouse enter events by updating mouse position.
     * Implements MouseListener interface.
     *
     * @param e The mouse event
     */
    public void mouseEntered(MouseEvent e) {
        mouseMoved(e);
    }

    /**
     * Handles mouse exit events by updating mouse position.
     * Implements MouseListener interface.
     *
     * @param e The mouse event
     */
    public void mouseExited(MouseEvent e) {
        mouseMoved(e);
    }

    /**
     * Handles mouse drag events by updating mouse position.
     * Implements MouseMotionListener interface.
     *
     * @param e The mouse event
     */
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    /**
     * Handles mouse movement events, processing relative motion if enabled.
     * Implements MouseMotionListener interface.
     *
     * @param e The mouse event
     */
    public synchronized void mouseMoved(MouseEvent e) {
        // this event is from re-centering the mouse - ignore it
        if (isRecentering &&
                centerLocation.x == e.getX() &&
                centerLocation.y == e.getY()) {
            isRecentering = false;
        } else {
            // Calculate mouse movement delta
            int dx = e.getX() - mouseLocation.x;
            int dy = e.getY() - mouseLocation.y;

            // Trigger appropriate mouse movement actions
            mouseHelper(MOUSE_MOVE_LEFT, MOUSE_MOVE_RIGHT, dx);
            mouseHelper(MOUSE_MOVE_UP, MOUSE_MOVE_DOWN, dy);

            // In relative mode, recenter the mouse after processing movement
            if (isRelativeMouseMode()) {
                recenterMouse();
            }
        }

        // Update stored mouse location
        mouseLocation.x = e.getX();
        mouseLocation.y = e.getY();
    }

    /**
     * Handles mouse wheel events by triggering the appropriate game action.
     * Implements MouseWheelListener interface.
     *
     * @param e The mouse wheel event
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseHelper(MOUSE_WHEEL_UP, MOUSE_WHEEL_DOWN,
                e.getWheelRotation());
    }

    /**
     * Helper method for handling directional mouse input.
     * Determines which game action to trigger based on the direction of movement.
     *
     * @param codeNeg The action code for negative movement
     * @param codePos The action code for positive movement
     * @param amount  The amount of movement (negative or positive)
     */
    private void mouseHelper(int codeNeg, int codePos,
            int amount) {
        GameAction gameAction;
        if (amount < 0) {
            gameAction = mouseActions[codeNeg];
        } else {
            gameAction = mouseActions[codePos];
        }
        if (gameAction != null) {
            gameAction.press(Math.abs(amount));
            gameAction.release();
        }
    }
}