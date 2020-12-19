package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TicketDAOTest {
    private static final int PARKING_NUMBER = 1;
    private static final double PRICE = 1.5;
    private static final boolean AVAILABLE = true;
    public static final String VEHICLE_REG_NUMBER_ABCDEF = "ABCDEF";
    private DataBaseTestConfig dataBaseTestConfig;
    private TicketDAO ticketDAO;

    @BeforeEach
    void setUpPerTest() {
        dataBaseTestConfig = new DataBaseTestConfig();
        ticketDAO = new TicketDAO(dataBaseTestConfig);
    }

    @AfterEach
    void afterEachTest() {
        DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void checkRecurrentUser() throws SQLException, ClassNotFoundException {
        setTicketInDatabase();
        setParkingInDatabase();

        assertThat(ticketDAO.checkRecurrentUser(VEHICLE_REG_NUMBER_ABCDEF)).isTrue();
    }

    @Test
    void checkNonRecurrentUser() {
        assertThat(ticketDAO.checkRecurrentUser(VEHICLE_REG_NUMBER_ABCDEF)).isFalse();
    }

    @Test
    void saveTicket() throws SQLException, ClassNotFoundException {
        ticketDAO.saveTicket(expectedTicket());
        final ResultSet ticketFromDatabase = getTicketFromDatabase();

        int parkingNumber = 1;
        Timestamp inTime = Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        Timestamp outTime = Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 1, 0, 0));
        double price = 1.5;

        if (ticketFromDatabase.next()) {
            parkingNumber = ticketFromDatabase.getInt(1);
            price = ticketFromDatabase.getDouble(3);
            inTime = ticketFromDatabase.getTimestamp(4);
            outTime = ticketFromDatabase.getTimestamp(5);
        }
        assertThat(inTime.toLocalDateTime()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        assertThat(outTime.toLocalDateTime()).isEqualTo(LocalDateTime.of(2020, 1, 1, 1, 0, 0));
        assertThat(parkingNumber).isEqualTo(PARKING_NUMBER);
        assertThat(price).isEqualTo(PRICE);
    }

    @Test
    void getTicket() throws SQLException, ClassNotFoundException {
        setTicketInDatabase();
        Ticket ticket = ticketDAO.getTicket(VEHICLE_REG_NUMBER_ABCDEF);
        final ResultSet ticketFromDatabase = getTicketFromDatabase();

        int parkingNumber = 1;
        Timestamp inTime = Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        Timestamp outTime = Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 1, 0, 0));
        double price = 1.5;

        if (ticketFromDatabase.next()) {
            parkingNumber = ticketFromDatabase.getInt(1);
            price = ticketFromDatabase.getDouble(3);
            inTime = ticketFromDatabase.getTimestamp(4);
            outTime = ticketFromDatabase.getTimestamp(5);
        }

        assertThat(inTime.toLocalDateTime()).isEqualTo(ticket.getInTime());
        assertThat(outTime.toLocalDateTime()).isEqualTo(ticket.getOutTime());
        assertThat(parkingNumber).isEqualTo(ticket.getParkingSpot().getId());
        assertThat(price).isEqualTo(ticket.getPrice());
    }

    @Test
    void updateParking() throws SQLException, ClassNotFoundException {
        setTicketInDatabase();

        Ticket mockedTicket = Mockito.mock(Ticket.class);
        Mockito.when(mockedTicket.getId()).thenReturn(1);
        final double updatedPrice = 0.0;
        Mockito.when(mockedTicket.getPrice()).thenReturn(updatedPrice);
        final LocalDateTime updatedOutDate = LocalDateTime.of(2020, 1, 1, 2, 0, 0);
        Mockito.when(mockedTicket.getOutTime()).thenReturn(updatedOutDate);

        boolean updatedTicket = ticketDAO.updateTicket(mockedTicket);

        final ResultSet ticketFromDatabase = getTicketFromDatabase();

        int parkingNumber = -1;
        int id = -1;
        double price = -1.0;
        Timestamp inTime = Timestamp.from(Instant.MIN);
        Timestamp outTime = Timestamp.from(Instant.MAX);

        if (ticketFromDatabase.next()) {
            parkingNumber = ticketFromDatabase.getInt(1);
            id = ticketFromDatabase.getInt(2);
            price = ticketFromDatabase.getDouble(3);
            inTime = ticketFromDatabase.getTimestamp(4);
            outTime = ticketFromDatabase.getTimestamp(5);
        }

        assertThat(updatedTicket).isTrue();
        assertThat(parkingNumber).isEqualTo(1);
        assertThat(id).isEqualTo(1);
        assertThat(price).isEqualTo(updatedPrice);
        assertThat(inTime.toLocalDateTime()).isEqualTo(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        assertThat(outTime.toLocalDateTime()).isEqualTo(updatedOutDate);
    }

    private void setParkingInDatabase() throws
            SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement
                = dataBaseTestConfig.getConnection()
                .prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
        preparedStatement.setBoolean(1, AVAILABLE);
        preparedStatement.setInt(2, PARKING_NUMBER);
        preparedStatement.execute();
    }

    private void setTicketInDatabase() throws
            SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement
                = dataBaseTestConfig.getConnection()
                .prepareStatement(DBConstants.SAVE_TICKET);
        preparedStatement.setInt(1, PARKING_NUMBER);
        preparedStatement.setString(2, VEHICLE_REG_NUMBER_ABCDEF);
        preparedStatement.setDouble(3, PRICE);
        preparedStatement.setTimestamp(4,
                Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 0, 0, 0)));
        preparedStatement.setTimestamp(5,
                Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 1, 0, 0)));
        preparedStatement.execute();
    }

    private Ticket expectedTicket() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        Ticket ticket = new Ticket();
        ticket.setPrice(PRICE);
        ticket.setInTime(LocalDateTime.of(2020, 1, 1, 0, 0, 0));
        ticket.setOutTime(LocalDateTime.of(2020, 1, 1, 1, 0, 0));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber(VEHICLE_REG_NUMBER_ABCDEF);
        return ticket;
    }

    private ResultSet getTicketFromDatabase()
            throws SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement
                = dataBaseTestConfig.getConnection()
                .prepareStatement(DBConstants.GET_TICKET);
        preparedStatement.setString(1, VEHICLE_REG_NUMBER_ABCDEF);
        return preparedStatement.executeQuery();
    }
}