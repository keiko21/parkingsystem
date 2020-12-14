package com.parkit.parkingsystem.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Interactive shell where user can see and interact.
 */
public class InteractiveShell {

    /**
     * The constant OPTION_ONE.
     */
    public static final int OPTION_ONE = 1;
    /**
     * The constant OPTION_TWO.
     */
    public static final int OPTION_TWO = 2;
    /**
     * The constant OPTION_THREE.
     */
    public static final int OPTION_THREE = 3;
    /**
     * Logger of the Interactive Shell.
     */
    private static final Logger LOGGER =
            LogManager.getLogger("InteractiveShell");


    /**
     * The constant parkingService.
     */
    private final ParkingService parkingService;

    /**
     * Instantiates a new Interactive shell.
     *
     * @param pParkingService the p parking service
     */
    public InteractiveShell(final ParkingService pParkingService) {
        parkingService = pParkingService;
    }

    /**
     * Load interface of the interactive shell.
     */
    public void loadInterface() {
        LOGGER.info("App initialized!!!");
        System.out.println("Welcome to Parking System!");

        boolean continueApp = true;


        while (continueApp) {
            loadMenu();
            int option = parkingService.getInputReaderUtil().readSelection();
            switch (option) {
                case OPTION_ONE:
                    parkingService.processIncomingVehicle();
                    break;
                case OPTION_TWO:
                    parkingService.processExitingVehicle();
                    break;
                case OPTION_THREE:
                    System.out.println("Exiting from the system!");
                    continueApp = false;
                    break;
                default:
                    System.out.println(
                            "Unsupported option. Please enter a number "
                                    + "corresponding to the provided menu");
            }
        }
    }

    /**
     * Load menu to user.
     */
    private static void loadMenu() {
        System.out.println("Please select an option. "
                + "Simply enter the number to choose an action");
        System.out.println("1 New Vehicle Entering - Allocate Parking Space");
        System.out.println("2 Vehicle Exiting - Generate Ticket Price");
        System.out.println("3 Shutdown System");
    }

}
