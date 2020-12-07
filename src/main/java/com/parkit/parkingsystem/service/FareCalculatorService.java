package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

public class FareCalculatorService {

    public static final double MULTIPLIER_OF_PRICE_DISCOUNT_RECURRENT_USER = 0.95;
    public static final double THIRTY_MINUTES = 0.5;

    public void calculateFare(Ticket ticket, boolean recurrentUser) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            String exceptionMessage = "Out time provided is incorrect.";
            if (ticket.getOutTime() != null) {
                exceptionMessage = "Out time provided is incorrect:" + ticket.getOutTime().toString();
            }
            throw new IllegalArgumentException(exceptionMessage);
        }

        final double parkingTimeInHours = (Duration.between(ticket.getInTime(), ticket.getOutTime()).toMinutes() / 60.0);

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                double price = Fare.CAR_RATE_PER_HOUR * parkingTimeInHours;
                if (recurrentUser) {
                    price *= MULTIPLIER_OF_PRICE_DISCOUNT_RECURRENT_USER;
                }
                if (parkingTimeInHours <= THIRTY_MINUTES) {
                    price = 0.0;
                }
                final double ceilPrice = Math.ceil(price * 100) / 100;
                ticket.setPrice(ceilPrice);
                break;
            }
            case BIKE: {
                double price = Fare.BIKE_RATE_PER_HOUR * parkingTimeInHours;
                if (recurrentUser) {
                    price *= MULTIPLIER_OF_PRICE_DISCOUNT_RECURRENT_USER;
                }
                if (parkingTimeInHours <= THIRTY_MINUTES) {
                    price = 0.0;
                }
                final double ceilPrice = Math.ceil(price * 100) / 100;
                ticket.setPrice(ceilPrice);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}