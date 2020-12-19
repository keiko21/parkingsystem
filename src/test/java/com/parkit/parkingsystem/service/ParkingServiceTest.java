package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {
    public static final String VEHICLE_REG_NUMBER_ABCDEF = "ABCDEF";
    public static final int CAR_TYPE_INPUT = 1;
    public static final int BIKE_TYPE_INPUT = 2;
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private ParkingService parkingService;
    @Mock
    private InputReaderUtil inputReaderUtil;
    @Mock
    private ParkingSpotDAO parkingSpotDAO;
    @Mock
    private TicketDAO ticketDAO;

    @BeforeEach
    public void setUpPerTest() {
        System.setOut(new PrintStream(outputStreamCaptor));
        parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn
                (VEHICLE_REG_NUMBER_ABCDEF);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    public void processExitingVehicleUpdateOnTicketTest() {
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.getTicket(anyString())).thenReturn(setTicket());
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processExitingVehicle();

        verify(ticketDAO, Mockito.times(1)).getTicket(VEHICLE_REG_NUMBER_ABCDEF);
        verify(ticketDAO, Mockito.times(1)).checkRecurrentUser(VEHICLE_REG_NUMBER_ABCDEF);
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));

        assertThat(outputStreamCaptor.toString().trim())
                .contains("Please pay the parking fare:",
                        "Recorded out-time for vehicle number:",
                        VEHICLE_REG_NUMBER_ABCDEF,
                        " is:");
        assertThat(outputStreamCaptor.toString().trim())
                .doesNotContain("Unable to update ticket information. "
                        + "Error occurred");
    }

    @Test
    public void processExitingVehicleNoUpdateOnTicketTest() {
        when(ticketDAO.getTicket(anyString())).thenReturn(setTicket());
        when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(false);

        parkingService.processExitingVehicle();

        verify(ticketDAO, Mockito.times(1)).getTicket(VEHICLE_REG_NUMBER_ABCDEF);
        verify(ticketDAO, Mockito.times(1)).checkRecurrentUser(VEHICLE_REG_NUMBER_ABCDEF);
        verify(ticketDAO, Mockito.times(1)).updateTicket(any(Ticket.class));
        verify(parkingSpotDAO, Mockito.times(0)).updateParking(any(ParkingSpot.class));

        assertThat(outputStreamCaptor.toString().trim())
                .doesNotContain("Please pay the parking fare:",
                        "Recorded out-time for vehicle number:",
                        VEHICLE_REG_NUMBER_ABCDEF,
                        " is:");
        assertThat(outputStreamCaptor.toString().trim())
                .contains("Unable to update ticket information. "
                        + "Error occurred");
    }

    @ParameterizedTest(name = "User input {0} for vehicle type")
    @ValueSource(ints = {CAR_TYPE_INPUT, BIKE_TYPE_INPUT})
    public void processIncomingVehicleNotRecurrentUserTest(int vehicleTypeInput) {
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.checkRecurrentUser(anyString())).thenReturn(false);
        when(inputReaderUtil.readSelection()).thenReturn(vehicleTypeInput);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).checkRecurrentUser(anyString());
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));

        assertThat(outputStreamCaptor.toString().trim())
                .contains("Generated Ticket and saved in DB",
                        "Please park your vehicle in spot number:",
                        "Recorded in-time for vehicle number:");
        assertThat(outputStreamCaptor.toString().trim())
                .doesNotContain("Welcome back! As a recurring user of our parking lot, " +
                        "you'll benefit from a 5% discount.");
    }

    @ParameterizedTest(name = "User input {0} for vehicle type")
    @ValueSource(ints = {CAR_TYPE_INPUT, BIKE_TYPE_INPUT})
    public void processIncomingVehicleRecurrentUserTest(int vehicleTypeInput) {
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        when(ticketDAO.checkRecurrentUser(anyString())).thenReturn(true);
        when(inputReaderUtil.readSelection()).thenReturn(vehicleTypeInput);
        when(parkingSpotDAO.getNextAvailableSlot(any())).thenReturn(1);

        parkingService.processIncomingVehicle();

        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(ticketDAO, Mockito.times(1)).checkRecurrentUser(anyString());
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));

        assertThat(outputStreamCaptor.toString().trim())
                .contains("Generated Ticket and saved in DB",
                        "Please park your vehicle in spot number:",
                        "Recorded in-time for vehicle number:");
        assertThat(outputStreamCaptor.toString().trim())
                .containsOnlyOnce("Welcome back! As a recurring user of our parking lot, " +
                        "you'll benefit from a 5% discount.");
    }

    private Ticket setTicket() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        Ticket ticket = new Ticket();
        ticket.setInTime(LocalDateTime.of(2020, 12, 1, 1, 0, 0));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER_ABCDEF);
        return ticket;
    }

}
