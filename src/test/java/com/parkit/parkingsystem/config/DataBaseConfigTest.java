package com.parkit.parkingsystem.config;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataBaseConfigTest {
    private DataBaseConfig dataBaseConfig;
    private PreparedStatement preparedStatement;
    private Connection connection;

    @BeforeEach
    void setUpPerTest() {
        dataBaseConfig = new DataBaseConfig();
    }

    @AfterEach
    void afterEachTest() {
        DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void getConnection() throws SQLException, ClassNotFoundException {
        assertThat(dataBaseConfig.getConnection()).isNotNull();
    }

    @Test
    void closeConnection() throws SQLException, ClassNotFoundException {
        connection = dataBaseConfig.getConnection();

        dataBaseConfig.closeConnection(connection);

        assertThat(connection.isClosed()).isTrue();
    }

    @Test
    void closedPreparedStatement() throws SQLException, ClassNotFoundException {
        connection = dataBaseConfig.getConnection();
        preparedStatement = connection.prepareStatement(DBConstants.GET_USER_RECURRENCES);

        dataBaseConfig.closePreparedStatement(preparedStatement);

        assertThat(preparedStatement.isClosed()).isTrue();
    }

    @Test
    void closeResultSet() throws SQLException, ClassNotFoundException {
        connection = dataBaseConfig.getConnection();
        preparedStatement = connection.prepareStatement(DBConstants.GET_USER_RECURRENCES);
        preparedStatement.setString(1, "");
        ResultSet resultSet = preparedStatement.executeQuery();

        dataBaseConfig.closeResultSet(resultSet);

        assertThat(resultSet.isClosed()).isTrue();
    }

}