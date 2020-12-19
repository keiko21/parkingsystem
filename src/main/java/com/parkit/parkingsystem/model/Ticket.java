package com.parkit.parkingsystem.model;

import java.time.LocalDateTime;

/**
 * The Ticket that contains all information relative to a ticket for a parking.
 */
public class Ticket {
    /**
     * The Id of a ticket.
     */
    private int id;
    /**
     * The Parking spot.
     */
    private ParkingSpot parkingSpot;
    /**
     * The Vehicle registration number.
     */
    private String vehicleRegNumber;
    /**
     * The Price.
     */
    private double price;
    /**
     * The In time.
     */
    private LocalDateTime inTime;
    /**
     * The Out time.
     */
    private LocalDateTime outTime;

    /**
     * Gets id of a ticket.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets id of a ticket.
     *
     * @param pId the id
     */
    public void setId(final int pId) {
        this.id = pId;
    }

    /**
     * Gets parking spot.
     *
     * @return the parking spot
     */
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    /**
     * Sets parking spot.
     *
     * @param pParkingSpot the parking spot
     */
    public void setParkingSpot(final ParkingSpot pParkingSpot) {
        this.parkingSpot = pParkingSpot;
    }

    /**
     * Gets vehicle registration number.
     *
     * @return the vehicle registration number
     */
    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    /**
     * Sets vehicle registration number.
     *
     * @param pVehicleRegNumber the vehicle registration number
     */
    public void setVehicleRegNumber(final String pVehicleRegNumber) {
        this.vehicleRegNumber = pVehicleRegNumber;
    }

    /**
     * Gets price of a parking.
     *
     * @return the price of a parking
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets price of a parking.
     *
     * @param pPrice the price of a parking.
     */
    public void setPrice(final double pPrice) {
        this.price = pPrice;
    }

    /**
     * Gets time entry of a parking.
     *
     * @return the in time of a parking
     */
    public LocalDateTime getInTime() {
        return inTime;
    }

    /**
     * Sets time entry of a parking.
     *
     * @param pInTime the in time of a parking
     */
    public void setInTime(final LocalDateTime pInTime) {
        this.inTime = pInTime;
    }

    /**
     * Gets exit time of a parking.
     *
     * @return the out time of a parking
     */
    public LocalDateTime getOutTime() {
        return outTime;
    }

    /**
     * Sets exit time of a parking.
     *
     * @param pOutTime the out time of a parking
     */
    public void setOutTime(final LocalDateTime pOutTime) {
        this.outTime = pOutTime;
    }
}
