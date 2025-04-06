package com.TETOSOFT.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JFrame;

/**
 * The ScreenManager class handles full-screen display mode operations for
 * games.
 * It provides functionality to find compatible display modes, set full-screen
 * mode,
 * manage graphics contexts, and handle screen updates using double buffering.
 */
public class ScreenManager {
    /** The graphics device (typically the monitor) being managed */
    private GraphicsDevice device;

    /**
     * Constructor initializes the ScreenManager with the default graphics device.
     */
    public ScreenManager() {
        GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = environment.getDefaultScreenDevice();
    }

    /**
     * Returns a list of all display modes compatible with the current graphics
     * device.
     *
     * @return Array of compatible DisplayMode objects
     */
    public DisplayMode[] getCompatibleDisplayModes() {
        return device.getDisplayModes();
    }

    /**
     * Finds the first display mode from the provided array that is compatible
     * with the current graphics device.
     *
     * @param modes Array of display modes to check for compatibility
     * @return The first compatible display mode, or null if none found
     */
    public DisplayMode findFirstCompatibleMode(DisplayMode modes[]) {
        DisplayMode goodModes[] = device.getDisplayModes();
        for (int i = 0; i < modes.length; i++) {
            for (int j = 0; j < goodModes.length; j++) {
                if (displayModesMatch(modes[i], goodModes[j])) {
                    return modes[i];
                }
            }
        }
        return null;
    }

    /**
     * Returns the current display mode of the graphics device.
     *
     * @return The current DisplayMode
     */
    public DisplayMode getCurrentDisplayMode() {
        return device.getDisplayMode();
    }

    /**
     * Checks if two display modes match based on width, height, bit depth, and
     * refresh rate.
     *
     * @param mode1 First display mode to compare
     * @param mode2 Second display mode to compare
     * @return true if the display modes match, false otherwise
     */
    public boolean displayModesMatch(DisplayMode mode1, DisplayMode mode2) {
        // Check if width and height match
        if (mode1.getWidth() != mode2.getWidth() || mode1.getHeight() != mode2.getHeight()) {
            return false;
        }

        // Check if bit depths match, accounting for multi-bit-depth mode
        if (mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
                mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
                mode1.getBitDepth() != mode2.getBitDepth()) {
            return false;
        }

        // Check if refresh rates match, accounting for unknown refresh rates
        if (mode1.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN &&
                mode2.getRefreshRate() != DisplayMode.REFRESH_RATE_UNKNOWN &&
                mode1.getRefreshRate() != mode2.getRefreshRate()) {
            return false;
        }

        return true;
    }

    /**
     * Sets the application to full-screen mode with the specified display mode.
     * Creates a new undecorated JFrame and sets up double buffering.
     *
     * @param displayMode The display mode to set, or null to use the current mode
     */
    public void setFullScreen(DisplayMode displayMode) {
        final JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setUndecorated(true); // Remove window decorations
        frame.setIgnoreRepaint(true); // Disable automatic repainting
        frame.setResizable(false);

        device.setFullScreenWindow(frame);

        // Change display mode if specified and supported
        if (displayMode != null && device.isDisplayChangeSupported()) {
            try {
                device.setDisplayMode(displayMode);
            } catch (IllegalArgumentException ex) {
            }

            frame.setSize(displayMode.getWidth(), displayMode.getHeight());
        }

        // Create buffer strategy for double buffering
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    frame.createBufferStrategy(2); // Double buffering
                }
            });
        } catch (InterruptedException ex) {
            // ignore
        } catch (InvocationTargetException ex) {
            // ignore
        }
    }

    /**
     * Gets the graphics context for drawing to the screen.
     *
     * @return A Graphics2D object for rendering, or null if not in full-screen mode
     */
    public Graphics2D getGraphics() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            return (Graphics2D) strategy.getDrawGraphics();
        }
        return null;
    }

    /**
     * Updates the display by showing the contents of the back buffer.
     * This method should be called after drawing operations are complete.
     */
    public void update() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }

        // Synchronize with the display refresh rate
        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Gets the full-screen window.
     *
     * @return The JFrame being used for full-screen display
     */
    public JFrame getFullScreenWindow() {
        return (JFrame) device.getFullScreenWindow();
    }

    /**
     * Gets the width of the full-screen window.
     *
     * @return The width in pixels, or 0 if not in full-screen mode
     */
    public int getWidth() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            return window.getWidth();
        } else {
            return 0;
        }
    }

    /**
     * Gets the height of the full-screen window.
     *
     * @return The height in pixels, or 0 if not in full-screen mode
     */
    public int getHeight() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            return window.getHeight();
        } else {
            return 0;
        }
    }

    /**
     * Restores the screen to normal windowed mode.
     * Releases full-screen exclusive mode and disposes of the window.
     */
    public void restoreScreen() {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            window.dispose();
        }
        device.setFullScreenWindow(null);
    }

    /**
     * Creates a hardware-accelerated compatible BufferedImage.
     * This method creates an image optimized for the current display hardware.
     *
     * @param w            Width of the image in pixels
     * @param h            Height of the image in pixels
     * @param transparancy Transparency type from BufferedImage transparency
     *                     constants
     * @return A compatible BufferedImage, or null if not in full-screen mode
     */
    public BufferedImage createCompatibleImage(int w, int h,
            int transparancy) {
        Window window = device.getFullScreenWindow();
        if (window != null) {
            GraphicsConfiguration gc = window.getGraphicsConfiguration();
            return gc.createCompatibleImage(w, h, transparancy);
        }
        return null;
    }
}