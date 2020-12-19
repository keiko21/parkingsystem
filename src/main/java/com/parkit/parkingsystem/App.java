package com.parkit.parkingsystem;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.service.InteractiveShell;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Main application.
 */
public final class App {
    /**
     * Logger constant for App class.
     *
     * @see Logger
     */
    private static final Logger LOGGER = LogManager.getLogger("App");

    /**
     * Databse configuration constant.
     */
    private static final DataBaseConfig DATABASE_CONFIG = new DataBaseConfig();

    /**
     * Instantiates a new App.
     */
    private App() {
    }

    /**
     * Entry of the main application.
     *
     * @param args the arguments of main application.
     */
    public static void main(final String[] args) {
        LOGGER.info("Initializing Parking System");

        ParkingSpotDAO parkingSpotDAO = new ParkingSpotDAO(DATABASE_CONFIG);
        TicketDAO ticketDAO = new TicketDAO(DATABASE_CONFIG);
        InputReaderUtil inputReaderUtil = new InputReaderUtil(new Scanner(
                System.in, StandardCharsets.UTF_8.name()));

        InteractiveShell interactiveShell =
                new InteractiveShell(new ParkingService(
                        inputReaderUtil,
                        parkingSpotDAO,
                        ticketDAO));

        interactiveShell.loadInterface();
    }
}
