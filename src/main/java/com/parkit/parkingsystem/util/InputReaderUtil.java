package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

/**
 * Input reader util that read user interactions.
 */
public class InputReaderUtil {

    /**
     * The scanner constant.
     */
    private final Scanner scanner;
    /**
     * Logger of Input Reader Util.
     */
    private static final Logger LOGGER =
            LogManager.getLogger("InputReaderUtil");

    /**
     * Instantiates a new Input reader util.
     *
     * @param pScanner the p scanner
     */
    public InputReaderUtil(final Scanner pScanner) {
        scanner = pScanner;
    }

    /**
     * Read user selection.
     *
     * @return the number entered.
     */
    public int readSelection() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            LOGGER.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. "
                    + "Please enter valid number for proceeding further");
            return -1;
        }
    }

    /**
     * Read vehicle registration number of a vehicle.
     *
     * @return the vehicle registration number.
     */
    public String readVehicleRegistrationNumber() {
        try {
            String vehicleRegNumber = scanner.nextLine();
            if (vehicleRegNumber == null
                    || vehicleRegNumber.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber;
        } catch (Exception e) {
            LOGGER.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. "
                    + "Please enter a valid string "
                    + "for vehicle registration number");
            throw e;
        }
    }


}
