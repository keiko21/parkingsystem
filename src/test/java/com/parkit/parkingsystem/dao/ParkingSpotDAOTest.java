package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParkingSpotDAOTest {
    private static final boolean AVAILABLE = true;
    private static final int PARKING_NUMBER = 1;
    private static DataBasePrepareService dataBasePrepareService;
    private static DataBaseTestConfig dataBaseTestConfig;
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static ParkingSpot parkingSpot;

    @BeforeAll
    static void setUp() {
        dataBaseTestConfig = new DataBaseTestConfig();
        dataBasePrepareService = new DataBasePrepareService();
        parkingSpotDAO = new ParkingSpotDAO(dataBaseTestConfig);
    }

    @BeforeEach
    void setUpPerTest() {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    void getNextAvailableSlotForCar() throws SQLException, ClassNotFoundException {
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
                = dataBaseTestConfig.getConnection()
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