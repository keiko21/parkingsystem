package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

/**
 * Parking service that do all treatment to process a vehicle on parking system.
 */
public class ParkingService {

    /**
     * Logger of the parking service.
     */
    private static final Logger LOGGER = LogManager.getLogger("ParkingService");

    /**
     * Fare calculator service constant.
     */
    private static final FareCalculatorService FARE_CALCULATOR_SERVICE =
            new FareCalculatorService();

    /**
     * The Input reader util.
     */
    private final InputReaderUtil inputReaderUtil;
    /**
     * The Parking spot dao.
     */
    private final ParkingSpotDAO parkingSpotDAO;

    /**
     * Gets input reader util.
     *
     * @return the input reader util
     */
    public InputReaderUtil getInputReaderUtil() {
        return inputReaderUtil;
    }

    /**
     * The Ticket dao.
     */
    private final TicketDAO ticketDAO;

    /**
     * Instantiates a new Parking service.
     *
     * @param pInputReaderUtil the input reader util that read user interactions
     * @param pParkingSpotDAO  the parking spot dao that manage parking spots
     * @param pTicketDAO       the ticket dao that manage a ticket parking
     */
    public ParkingService(final InputReaderUtil pInputReaderUtil,
                          final ParkingSpotDAO pParkingSpotDAO,
                          final TicketDAO pTicketDAO) {
        this.inputReaderUtil = pInputReaderUtil;
        this.parkingSpotDAO = pParkingSpotDAO;
        this.ticketDAO = pTicketDAO;
    }

    /**
     * Process incoming vehicle to parking.
     */
    public void processIncomingVehicle() {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if (parkingSpot != null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehichleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot);

                LocalDateTime inTime = LocalDateTime.now();
                Ticket ticket = new Ticket();
                if (ticketDAO.checkRecurrentUser(vehicleRegNumber)) {
                    System.out.println(
                            "Welcome back! As a recurring user of our"
                                    + " parking lot, "
                                    + "you'll benefit from a 5% discount.");
                }
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setInTime(inTime);
                ticket.setOutTime(null);
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:"
                        + parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:"
                        + vehicleRegNumber
                        + " is:"
                        + inTime);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to process incoming vehicle", e);
        }
    }

    /**
     * Process exiting vehicle from parking.
     */
    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            LocalDateTime outTime = LocalDateTime.now();
            ticket.setOutTime(outTime);
            FARE_CALCULATOR_SERVICE.calculateFare(ticket,
                    ticketDAO.checkRecurrentUser(vehicleRegNumber));
            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:"
                        + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:"
                        + ticket.getVehicleRegNumber()
                        + " is:"
                        + outTime);
            } else {
                System.out.println("Unable to update ticket information. "
                        + "Error occurred");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to process exiting vehicle", e);
        }
    }

    /**
     * Get next parking number if available parking spot.
     *
     * @return the parking spot available
     */
    private ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB. "
                        + "Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            LOGGER.error("Error parsing user input for type of vehicle", ie);
        } catch (Exception e) {
            LOGGER.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    /**
     * Gets vehicle type.
     *
     * @return the vehicle type
     */
    private ParkingType getVehichleType() {
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case 1:
                return ParkingType.CAR;
            case 2:
                return ParkingType.BIKE;
            default:
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
        }
    }

    /**
     * Gets vehicle reg number.
     *
     * @return the vehicle registration number
     */
    private String getVehichleRegNumber() {
        System.out.println("Please type the vehicle registration number "
                + "and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }
}
