package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Input reader util that read user interactions.
 */
public class InputReaderUtil {

    /**
     * The scanner constant.
     */
    private static final Scanner SCANNER
            = new Scanner(System.in, StandardCharsets.UTF_8.name());
    /**
     * Logger of Input Reader Util.
     */
    private static final Logger LOGGER =
            LogManager.getLogger("InputReaderUtil");

    /**
     * Read user selection.
     *
     * @return the number entered.
     */
    public int readSelection() {
        try {
            return Integer.parseInt(SCANNER.nextLine());
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
            String vehicleRegNumber = SCANNER.nextLine();
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
