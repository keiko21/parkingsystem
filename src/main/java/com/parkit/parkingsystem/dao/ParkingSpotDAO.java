package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.parkit.parkingsystem.config.DataBaseConfig.setDatabaseValues;

/**
 * A parking spot manager.
 */
public class ParkingSpotDAO {
    /**
     * Logger of parking spot DAO.
     */
    private static final Logger LOGGER = LogManager.getLogger("ParkingSpotDAO");

    /**
     * The Data base configuration.
     */
    private final DataBaseConfig dataBaseConfig;

    /**
     * Instantiates a new Parking spot dao.
     *
     * @param pDataBaseConfig the data base configuration
     */
    public ParkingSpotDAO(final DataBaseConfig pDataBaseConfig) {
        this.dataBaseConfig = pDataBaseConfig;
    }

    /**
     * Gets next available slot of a parking.
     *
     * @param parkingType the parking type
     * @return the next available slot
     */
    public int getNextAvailableSlot(final ParkingType parkingType) {
        Connection con;
        int result = -1;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.GET_NEXT_PARKING_SPOT);
            Object[] databaseValues = {
                    parkingType.toString()
            };
            setDatabaseValues(ps, databaseValues);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getInt(DBConstants.MIN_PARKING_NUMBER_COLUMN_LABEL);
            }
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closeConnection(con);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("Error fetching next available slot", e);
        }
        return result;
    }

    /**
     * Update a parking spot.
     *
     * @param parkingSpot the parking spot
     * @return if the parking spot is updated or not
     */
    public boolean updateParking(final ParkingSpot parkingSpot) {
        Connection con;
        int updateRowCount = -1;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.UPDATE_PARKING_SPOT);
            Object[] databaseValues = {
                    parkingSpot.isAvailable(),
                    parkingSpot.getId()
            };
            setDatabaseValues(ps, databaseValues);
            updateRowCount = ps.executeUpdate();

            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.error("Error updating parking info", e);
        }
        return (updateRowCount == 1);
    }
}
