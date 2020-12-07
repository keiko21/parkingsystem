package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

        fareCalculatorService.calculateFare(ticket, false);

        assertThat(ticket.getPrice()).isEqualTo(1.5);
    }

    @Test
    public void calculateFareBike() {
        Ticket ticket = setTicket(60, ParkingType.BIKE);

        fareCalculatorService.calculateFare(ticket, false);

        assertThat(ticket.getPrice()).isEqualTo(1.0);
    }

    @Test
    public void calculateFareCarThirtyMinutes() {
        ticket = setTicket(30, ParkingType.CAR);

        fareCalculatorService.calculateFare(ticket, false);

        assertThat(ticket.getPrice()).isEqualTo(0.0);
    }

    @Test
    public void calculateFareBikeThirtyMinutes() {
        Ticket ticket = setTicket(30, ParkingType.BIKE);

        fareCalculatorService.calculateFare(ticket, false);

        assertThat(ticket.getPrice()).isEqualTo(0.0);
    }

    @Test
    public void calculateFareUnkownType() {
        Ticket ticket = setTicket(60, null);

        assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
        Ticket ticket = setTicket(-60, ParkingType.BIKE);

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> fareCalculatorService.calculateFare(ticket, false));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime(){
        Ticket ticket = setTicket(45, ParkingType.BIKE);

        fareCalculatorService.calculateFare(ticket, false);

        assertThat(ticket.getPrice()).isEqualTo(0.75);
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime(){
        Ticket ticket = setTicket(45, ParkingType.CAR);

        fareCalculatorService.calculateFare(ticket, false);

        assertThat(ticket.getPrice()).isEqualTo(1.13);
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
        Ticket ticket = setTicket(24 * 60, ParkingType.CAR);

        fareCalculatorService.calculateFare(ticket, false);

        assertThat(ticket.getPrice()).isEqualTo(36);
    }

    @Test
    public void calculateFareCarRecurrentUser() {
        ticket = setTicket(60, ParkingType.CAR);

        fareCalculatorService.calculateFare(ticket, true);

        assertThat(ticket.getPrice()).isEqualTo(1.43);
    }

    @Test
    public void calculateFareBikeRecurrentUser() {
        Ticket ticket = setTicket(60, ParkingType.BIKE);

        fareCalculatorService.calculateFare(ticket, true);

        assertThat(ticket.getPrice()).isEqualTo(0.95);
    }

    private Ticket setTicket(int parkingTimeInMinutes, ParkingType parkingType) {
        LocalDateTime inTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
        LocalDateTime outTime = inTime.plusMinutes(parkingTimeInMinutes);

        ParkingSpot parkingSpot = new ParkingSpot(1, parkingType, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        return ticket;
    }
}
