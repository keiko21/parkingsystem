package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingSpotDAOTest {
    private static final boolean AVAILABLE = true;
    private static final int PARKING_NUMBER = 1;
    private DataBaseTestConfig dataBaseTestConfig;
    private ParkingSpotDAO parkingSpotDAO;
    @Mock
    private ParkingSpot parkingSpot;

    @BeforeEach
    void setUpPerTest() {
        dataBaseTestConfig = new DataBaseTestConfig();
        parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
    }

    @AfterEach
    void afterEachTest() {
        DataBasePrepareService dataBasePrepareService = new DataBasePrepareService();
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void getNextAvailableSlot() throws SQLException,
            ClassNotFoundException {
        setAvailableFirstParkingSpotIntoDatabase();

        assertThat(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).isEqualTo(1);
    }

    @Test
    void updateAvailableParking() throws SQLException, ClassNotFoundException {
        when(parkingSpot.getId()).thenReturn(PARKING_NUMBER);
        when(parkingSpot.isAvailable()).thenReturn(AVAILABLE);

        assertThat(parkingSpotDAO.updateParking(parkingSpot)).isTrue();
        assertThat(getNextParkingSpotAvailableFromDatabase()).isEqualTo(1);
    }

    private void setAvailableFirstParkingSpotIntoDatabase() throws
            SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement
                =
                dataBaseTestConfig.getConnection()
                        .prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
        preparedStatement.setBoolean(1, AVAILABLE);
        preparedStatement.setInt(2, PARKING_NUMBER);
        preparedStatement.execute();
    }

    private int getNextParkingSpotAvailableFromDatabase()
            throws SQLException, ClassNotFoundException {
        final PreparedStatement preparedStatement
                = dataBaseTestConfig.getConnection()
                .prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
        preparedStatement.setString(1, ParkingType.CAR.toString());

        final ResultSet resultSet = preparedStatement.executeQuery();

        int parkingNumberAvailable = 0;
        if (resultSet.next()) {
            parkingNumberAvailable = resultSet.getInt(1);
        }
        return parkingNumberAvailable;
    }
}