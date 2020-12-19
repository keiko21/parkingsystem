package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class FareCalculatorServiceTest {

    public static final int ONE_HOUR = 60;
    public static final int ONE_DAY = 24 * 60;
    public static final int THIRTY_MINUTES = 30;
    public static final int FOURTY_FIVE_MINUTES = 45;

    private FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeEach
    public void setUpPerTest() {
        fareCalculatorService = new FareCalculatorService();
        ticket = new Ticket();
    }

    private static Stream<Arguments> calculateFareArguments() {
        return Stream.of(
                Arguments.of(ONE_HOUR, ParkingType.CAR, 1.5),
                Arguments.of(ONE_HOUR, ParkingType.BIKE, 1.0),
                Arguments.of(THIRTY_MINUTES, ParkingType.CAR, 0.0),
                Arguments.of(THIRTY_MINUTES, ParkingType.BIKE, 0.0),
                Arguments.of(FOURTY_FIVE_MINUTES, ParkingType.CAR, 1.13),
                Arguments.of(FOURTY_FIVE_MINUTES, ParkingType.BIKE, 0.75),
                Arguments.of(ONE_DAY, ParkingType.CAR, 36.0),
                Arguments.of(ONE_DAY, ParkingType.BIKE, 24.0)
        );
    }

    private static Stream<Arguments> throwExceptionWhenCalculatingFareArguments() {
        return Stream.of(
                Arguments.of(ONE_HOUR, null, NullPointerException.class),
                Arguments.of(-ONE_HOUR, ParkingType.CAR, IllegalArgumentException.class),
                Arguments.of(-ONE_HOUR, ParkingType.BIKE, IllegalArgumentException.class)
        );
    }

    private static Stream<Arguments> calculateFareRecurrentUserArguments() {
        return Stream.of(
                Arguments.of(ONE_HOUR, ParkingType.CAR, 1.43),
                Arguments.of(ONE_HOUR, ParkingType.BIKE, 0.95),
                Arguments.of(THIRTY_MINUTES, ParkingType.CAR, 0.0),
                Arguments.of(THIRTY_MINUTES, ParkingType.BIKE, 0.0),
                Arguments.of(FOURTY_FIVE_MINUTES, ParkingType.CAR, 1.07),
                Arguments.of(FOURTY_FIVE_MINUTES, ParkingType.BIKE, 0.72),
                Arguments.of(ONE_DAY, ParkingType.CAR, 34.2),
                Arguments.of(ONE_DAY, ParkingType.BIKE, 22.8)
        );
    }

    @ParameterizedTest
    @MethodSource("calculateFareArguments")
    public void calculateFare(int parkingTimeInMinutes,
                              ParkingType parkingType,
                              double expectedPrice) {
        ticket = setTicket(parkingTimeInMinutes, parkingType);

        fareCalculatorService.calculateFare(ticket, false);

        assertThat(ticket.getPrice()).isEqualTo(expectedPrice);
    }

    @ParameterizedTest
    @MethodSource("throwExceptionWhenCalculatingFareArguments")
    public void throwExceptionWhenCalculatingFare(int parkingTimeInMinutes,
                                                  ParkingType parkingType,
                                                  Class<Throwable> exceptionClass) {
        ticket = setTicket(parkingTimeInMinutes, parkingType);

        assertThatExceptionOfType(exceptionClass)
                .isThrownBy(() -> fareCalculatorService.calculateFare(ticket, false));
    }

    @ParameterizedTest
    @MethodSource("calculateFareRecurrentUserArguments")
    public void calculateFareRecurrentUser(int parkingTimeInMinutes,
                                           ParkingType parkingType,
                                           double expectedPrice) {
        ticket = setTicket(parkingTimeInMinutes, parkingType);

        fareCalculatorService.calculateFare(ticket, true);

        assertThat(ticket.getPrice()).isEqualTo(expectedPrice);
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
