package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.util.concurrent.TimeUnit;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            String exceptionMessage = "Out time provided is incorrect.";
            if (ticket.getInTime() != null) {
                exceptionMessage = "Out time provided is incorrect:" + ticket.getOutTime().toString();
            }
            throw new IllegalArgumentException(exceptionMessage);
        }

        final long parkingTimeInMilliseconds = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        final double numberMillisecondsInOneHour = TimeUnit.HOURS.toMillis(1);
        double parkingTimeInHours = parkingTimeInMilliseconds / numberMillisecondsInOneHour;

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR: {
                ticket.setPrice(parkingTimeInHours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(parkingTimeInHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown Parking Type");
        }
    }
}