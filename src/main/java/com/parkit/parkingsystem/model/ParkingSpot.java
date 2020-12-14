package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

/**
 * Contains all information relative to a parking spot.
 */
public class ParkingSpot {
    /**
     * The parking spot number.
     */
    private final int number;
    /**
     * The Parking type.
     */
    private final ParkingType parkingType;
    /**
     * Is available parking spot.
     */
    private boolean isAvailable;

    /**
     * Instantiates a new Parking spot.
     *
     * @param pNumber      the Number of a parking
     * @param pParkingType the parking type
     * @param pIsAvailable availability of a parking spot
     */
    public ParkingSpot(final int pNumber,
                       final ParkingType pParkingType,
                       final boolean pIsAvailable) {
        this.number = pNumber;
        this.parkingType = pParkingType;
        this.isAvailable = pIsAvailable;
    }

    /**
     * Gets the number of a parking.
     *
     * @return the id of a parking
     */
    public int getId() {
        return number;
    }

    /**
     * Gets parking type.
     *
     * @return the parking type
     */
    public ParkingType getParkingType() {
        return parkingType;
    }

    /**
     * Is available parking spot.
     *
     * @return if is available or not
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Sets availability of a parking spot.
     *
     * @param available if is available or not
     */
    public void setAvailable(final boolean available) {
        isAvailable = available;
    }
}
