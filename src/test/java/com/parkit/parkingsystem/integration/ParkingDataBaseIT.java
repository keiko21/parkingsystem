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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    static final String vehicleRegistrationNumber = "ABCDEF";
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    @BeforeAll
    static void setUp() {
        parkingSpotDAO = new ParkingSpotDAO();
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @BeforeEach
    void setUpPerTest() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(vehicleRegistrationNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    public void testParkingACar() throws SQLException, ClassNotFoundException {
        when(inputReaderUtil.readSelection()).thenReturn(1);

        parkACar();

        final ResultSet ticketFromDatabase = getTicketFromDatabase();

        int parkingNumber = 0;
        Timestamp inTime = Timestamp.from(Instant.MIN);
        String parkingType = null;
        boolean available = true;

        if (ticketFromDatabase.next()) {
            parkingNumber = ticketFromDatabase.getInt(1);
            inTime = ticketFromDatabase.getTimestamp(4);
            parkingType = ticketFromDatabase.getString(6);
            available = ticketFromDatabase.getBoolean(7);
        }

        final Ticket ticket = ticketDAO.getTicket(vehicleRegistrationNumber);

        assertThat(inTime.toLocalDateTime()).isEqualTo(ticket.getInTime());
        assertThat(parkingNumber).isEqualTo(ticket.getParkingSpot().getId());
        assertThat(available).isEqualTo(ticket.getParkingSpot().isAvailable());
        assertThat(ParkingType.valueOf(parkingType)).isEqualTo(ticket.getParkingSpot().getParkingType());

    }

    @Test
    public void testParkingLotExit() throws SQLException, ClassNotFoundException {
        ParkingService parkingService = parkACarWithFakeTicket();
        parkingService.processExitingVehicle();

        final ResultSet resultSet = getTicketFromDatabase();

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

    private ResultSet getTicketFromDatabase() throws SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement = dataBaseTestConfig.getConnection().prepareStatement(DBConstants.GET_TICKET);
        preparedStatement.setString(1, vehicleRegistrationNumber);
        return preparedStatement.executeQuery();
    }

    private void parkACar() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
    }

    private ParkingService parkACarWithFakeTicket() {
        Ticket fakeTicket = new Ticket();
        fakeTicket.setId(1);
        fakeTicket.setVehicleRegNumber(vehicleRegistrationNumber);
        fakeTicket.setInTime(LocalDateTime.now().minusHours(1));
        fakeTicket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        fakeTicket.setPrice(0.0);
        ticketDAO.saveTicket(fakeTicket);

        return new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }
}
