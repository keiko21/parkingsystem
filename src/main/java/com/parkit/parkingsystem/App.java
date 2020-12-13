package com.parkit.parkingsystem;

import com.parkit.parkingsystem.service.InteractiveShell;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main application.
 */
public final class App {
    /**
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger("App");

    private App() {
    }

    /**
     * Entry of the main application.
     *
     * @param args the arguments of main application.
     */
    public static void main(final String[] args) {
        LOGGER.info("Initializing Parking System");
        InteractiveShell.loadInterface();
    }
}
