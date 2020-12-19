package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    static final String CAR_REGISTRATION_NUMBER = "CAR";
    static final String BIKE_REGISTRATION_NUMBER = "BIKE";
    private ParkingSpotDAO parkingSpotDAO;
    private TicketDAO ticketDAO;
    @Mock
    private InputReaderUtil inputReaderUtil;
    private DataBaseTestConfig dataBaseTestConfig;

    private static Stream<Arguments> testParkingArguments() {
        return Stream.of(
                Arguments.of(ParkingType.CAR, CAR_REGISTRATION_NUMBER, 1),
                Arguments.of(ParkingType.BIKE, BIKE_REGISTRATION_NUMBER, 2)
        );
    }

    @BeforeEach
    void setUpPerTest() {
        dataBaseTestConfig = new DataBaseTestConfig();
        parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
        ticketDAO = new TicketDAO(dataBaseTestConfig);
    }

    @AfterEach
    void afterEachTest() {
        DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @ParameterizedTest
    @MethodSource("testParkingArguments")
    public void testParking(ParkingType parkingType,
                            String vehicleRegistrationNumber,
                            int option) throws SQLException,
            ClassNotFoundException {
        when(inputReaderUtil.readSelection()).thenReturn(option);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn
                (vehicleRegistrationNumber);

        park(parkingType);

        final ResultSet ticketFromDatabase = getTicketFromDatabase(vehicleRegistrationNumber);

        int parkingNumber = 0;
        Timestamp inTime = Timestamp.from(Instant.MIN);
        String parkingTypeDB = null;
        boolean available = true;

        if (ticketFromDatabase.next()) {
            parkingNumber = ticketFromDatabase.getInt(1);
            inTime = ticketFromDatabase.getTimestamp(4);
            parkingTypeDB = ticketFromDatabase.getString(6);
            available = ticketFromDatabase.getBoolean(7);
        }

        final Ticket ticket = ticketDAO.getTicket(vehicleRegistrationNumber);

        assertThat(inTime.toLocalDateTime()).isEqualTo(ticket.getInTime());
        assertThat(parkingNumber).isEqualTo(ticket.getParkingSpot().getId());
        assertThat(available).isEqualTo(ticket.getParkingSpot().isAvailable());
        assertThat(ParkingType.valueOf(parkingTypeDB))
                .isEqualTo(ticket.getParkingSpot().getParkingType());

    }

    @ParameterizedTest
    @MethodSource("testParkingArguments")
    public void testParkingRecurrentUser(ParkingType parkingType,
                                         String vehicleRegistrationNumber,
                                         int option)
            throws SQLException, ClassNotFoundException {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn
                (vehicleRegistrationNumber);
        ParkingService parkingService = parkVehicleWithFakeTicket(parkingType,
                vehicleRegistrationNumber);
        parkingService.processExitingVehicle();
        when(inputReaderUtil.readSelection()).thenReturn(option);
        park(parkingType);

        final ResultSet ticketFromDatabase = getTicketFromDatabase(vehicleRegistrationNumber);

        int parkingNumber = 0;
        Timestamp inTime = Timestamp.from(Instant.MIN);
        String parkingTypeDB = null;

        if (ticketFromDatabase.next()) {
            parkingNumber = ticketFromDatabase.getInt(1);
            inTime = ticketFromDatabase.getTimestamp(4);
            parkingTypeDB = ticketFromDatabase.getString(6);
        }

        final Ticket ticket = ticketDAO.getTicket(vehicleRegistrationNumber);

        assertThat(inTime.toLocalDateTime()).isEqualTo(ticket.getInTime());
        assertThat(parkingNumber).isEqualTo(ticket.getParkingSpot().getId());
        assertThat(ParkingType.valueOf(parkingTypeDB))
                .isEqualTo(ticket.getParkingSpot().getParkingType());
    }

    @ParameterizedTest
    @MethodSource("testParkingArguments")
    public void testParkingLotExit(ParkingType parkingType,
                                   String vehicleRegistrationNumber)
            throws SQLException, ClassNotFoundException {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn
                (vehicleRegistrationNumber);
        ParkingService parkingService =
                parkVehicleWithFakeTicket(parkingType, vehicleRegistrationNumber);
        parkingService.processExitingVehicle();

        final ResultSet resultSet = getTicketFromDatabase(vehicleRegistrationNumber);

        double price = 0;
        Timestamp outTime = Timestamp.from(Instant.MIN);
        if (resultSet.next()) {
            price = resultSet.getDouble(3);
            outTime = resultSet.getTimestamp(5);
        }

        Ticket ticket = ticketDAO.getTicket(vehicleRegistrationNumber);

        assertThat(price).isEqualTo(ticket.getPrice());
        assertThat(outTime.toLocalDateTime()).isEqualTo(ticket.getOutTime());
    }

    @ParameterizedTest
    @MethodSource("testParkingArguments")
    public void testParkingLotExitRecurrentUser(ParkingType parkingType,
                                                String vehicleRegistrationNumber)
            throws SQLException, ClassNotFoundException {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn
                (vehicleRegistrationNumber);
        ParkingService parkingService =
                parkVehicleWithFakeTicket(parkingType, vehicleRegistrationNumber);
        parkingService.processExitingVehicle();
        parkingService = parkVehicleWithFakeTicket(parkingType, vehicleRegistrationNumber);
        parkingService.processExitingVehicle();

        final ResultSet resultSet = getTicketFromDatabase(vehicleRegistrationNumber);

        double price = 0;
        Timestamp outTime = Timestamp.from(Instant.MIN);
        if (resultSet.next()) {
            price = resultSet.getDouble(3);
            outTime = resultSet.getTimestamp(5);
        }

        Ticket ticket = ticketDAO.getTicket(vehicleRegistrationNumber);

        assertThat(price).isEqualTo(ticket.getPrice());
        assertThat(outTime.toLocalDateTime()).isEqualTo(ticket.getOutTime());
    }

    private ResultSet getTicketFromDatabase(String vehicleRegistrationNumber)
            throws SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement
                = dataBaseTestConfig.getConnection()
                .prepareStatement(DBConstants.GET_TICKET);
        preparedStatement.setString(1, vehicleRegistrationNumber);
        return preparedStatement.executeQuery();
    }

    private void park(ParkingType parkingType) {
        ParkingService parkingService
                = new ParkingService(
                inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingSpotDAO.getNextAvailableSlot(parkingType);
    }

    private ParkingService parkVehicleWithFakeTicket(ParkingType parkingType,
                                                     String vehicleRegistrationNumber) {
        Ticket fakeTicket = new Ticket();
        fakeTicket.setId(1);
        fakeTicket.setVehicleRegNumber(vehicleRegistrationNumber);
        fakeTicket.setInTime(LocalDateTime.now().minusHours(1));
        fakeTicket.setParkingSpot(
                new ParkingSpot(1, parkingType, false));
        fakeTicket.setPrice(0.0);
        ticketDAO.saveTicket(fakeTicket);

        return new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }
}
