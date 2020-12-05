package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.time.Duration;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
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
                double price = Math.ceil(Fare.CAR_RATE_PER_HOUR * parkingTimeInHours * 100) / 100;
                ticket.setPrice(price);
                break;
            }
            case BIKE: {
                double price = Math.floor(Fare.BIKE_RATE_PER_HOUR * parkingTimeInHours * 100) / 100;
                ticket.setPrice(price);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}