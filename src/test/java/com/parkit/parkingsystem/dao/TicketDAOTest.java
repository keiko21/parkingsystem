package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TicketDAOTest {
    private static final int PARKING_NUMBER = 1;
    private static final double PRICE = 1.5;
    private static final boolean AVAILABLE = true;
    private static final String VEHICLE_REG_NUMBER = "ABCDEF";
    private static DataBasePrepareService dataBasePrepareService;
    private static DataBaseTestConfig dataBaseTestConfig;
    private static TicketDAO ticketDAO;

    @BeforeAll
    static void setUp() {
        dataBaseTestConfig = new DataBaseTestConfig();
        dataBasePrepareService = new DataBasePrepareService();
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
    }

    @BeforeEach
    void setUpPerTest() {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void checkRecurrentUser() throws SQLException, ClassNotFoundException {
        setTicketInDatabase();
        setParkingInDatabase();

        assertThat(ticketDAO.checkRecurrentUser(VEHICLE_REG_NUMBER)).isTrue();
    }

    @Test
    void checkNonRecurrentUser() {
        assertThat(ticketDAO.checkRecurrentUser(VEHICLE_REG_NUMBER)).isFalse();
    }

    private void setParkingInDatabase() throws SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement = dataBaseTestConfig.getConnection().prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
        preparedStatement.setBoolean(1, AVAILABLE);
        preparedStatement.setInt(2, PARKING_NUMBER);
        preparedStatement.execute();
    }

    private void setTicketInDatabase() throws SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement = dataBaseTestConfig.getConnection().prepareStatement(DBConstants.SAVE_TICKET);
        preparedStatement.setInt(1, PARKING_NUMBER);
        preparedStatement.setString(2, VEHICLE_REG_NUMBER);
        preparedStatement.setDouble(3, PRICE);
        preparedStatement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 0, 0, 0)));
        preparedStatement.setTimestamp(5, Timestamp.valueOf(LocalDateTime.of(2020, 1, 1, 1, 0, 0)));
        preparedStatement.execute();
    }
}