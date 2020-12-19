package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

/**
 * The Fare calculator service that calculate fare for a parking.
 */
public class FareCalculatorService {

    /**
     * Price for thirty minutes constant.
     */
    public static final double PRICE_FOR_THIRTY_MINUTES = 0.0;
    /**
     * The constant ONE_HUNDRED.
     */
    public static final int ONE_HUNDRED = 100;
    /**
     * Multiplier of price discount of recurrent user constant.
     */
    private static final double
            MULTIPLIER_OF_PRICE_DISCOUNT_RECURRENT_USER = 0.95;
    /**
     * Thirty minutes constant.
     */
    private static final double THIRTY_MINUTES = 0.5;

    /**
     * Calculate fare from a ticket.
     *
     * @param ticket        the ticket
     * @param recurrentUser to know if the user is a recurrent one
     */
    public void calculateFare(
            final Ticket ticket,
            final boolean recurrentUser) {
        if ((ticket.getOutTime() == null)
                || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            String exceptionMessage = "Out time provided is incorrect.";
            if (ticket.getOutTime() != null) {
                exceptionMessage = "Out time provided is incorrect:"
                        + ticket.getOutTime().toString();
            }
            throw new IllegalArgumentException(exceptionMessage);
        }

        final double parkingTimeInHours = (Duration.between(ticket.getInTime(),
                ticket.getOutTime()).toMinutes() / 60.0);

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                double carCeilPrice = getCeilPrice(
                        recurrentUser,
                        parkingTimeInHours,
                        Fare.CAR_RATE_PER_HOUR);
                ticket.setPrice(carCeilPrice);
                break;
            case BIKE:
                double bikeCeilPrice = getCeilPrice(
                        recurrentUser,
                        parkingTimeInHours,
                        Fare.BIKE_RATE_PER_HOUR);
                ticket.setPrice(bikeCeilPrice);
                break;
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }

    /**
     * Gets ceil price for parking.
     *
     * @param recurrentUser      if recurrent user
     * @param parkingTimeInHours the parking time in hours
     * @param carRatePerHour     the car rate per hour
     * @return the ceil price
     */
    private double getCeilPrice(final boolean recurrentUser,
                                final double parkingTimeInHours,
                                final double carRatePerHour) {
        double price = PRICE_FOR_THIRTY_MINUTES;
        if (recurrentUser && parkingTimeInHours > THIRTY_MINUTES) {
            price = carRatePerHour
                    * parkingTimeInHours
                    * MULTIPLIER_OF_PRICE_DISCOUNT_RECURRENT_USER;
        } else if (parkingTimeInHours > THIRTY_MINUTES) {
            price = carRatePerHour * parkingTimeInHours;
        }
        return Math.ceil(price * ONE_HUNDRED) / ONE_HUNDRED;
    }
}
