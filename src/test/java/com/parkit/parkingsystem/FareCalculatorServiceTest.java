package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;

    private Ticket ticket;

    @BeforeEach
    public void setUpPerTest() {
        fareCalculatorService = new FareCalculatorService();
        ticket = new Ticket();
    }

    @Test
    public void calculateFareCar(){
        ticket = setTicket(60, ParkingType.CAR);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(ticket.getPrice(), expectedFare(Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    public void calculateFareBike(){
        Ticket ticket = setTicket(60, ParkingType.BIKE);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(ticket.getPrice(), expectedFare(Fare.BIKE_RATE_PER_HOUR));
    }

    @Test
    public void calculateFareUnkownType(){
        Ticket ticket = setTicket(60, null);

        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime(){
        Ticket ticket = setTicket(-60, ParkingType.BIKE);

        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Ticket ticket = setTicket(45, ParkingType.BIKE);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(ticket.getPrice(), expectedFare(Fare.BIKE_RATE_PER_HOUR));
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Ticket ticket = setTicket(45, ParkingType.CAR);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(ticket.getPrice(), expectedFare(Fare.CAR_RATE_PER_HOUR));
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime(){
        Ticket ticket = setTicket(24 * 60, ParkingType.CAR);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(ticket.getPrice(), expectedFare(Fare.CAR_RATE_PER_HOUR));
    }

    private Ticket setTicket(int parkingTimeInMinutes, ParkingType parkingType) {
        Date inTime = new Date();
        final long parkingTimeInMilliseconds = TimeUnit.MINUTES.toMillis(parkingTimeInMinutes);
        inTime.setTime(System.currentTimeMillis() - parkingTimeInMilliseconds);

        Date outTime = new Date();

        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        return ticket;
    }

    private double expectedFare(double fareType) {
        final long parkingTimeInMilliseconds = ticket.getOutTime().getTime() - ticket.getInTime().getTime();
        final double numberMillisecondsInOneHour = TimeUnit.HOURS.toMillis(1);
        double parkingTimeInHours = parkingTimeInMilliseconds / numberMillisecondsInOneHour;

        return parkingTimeInHours * fareType;
    }
}
