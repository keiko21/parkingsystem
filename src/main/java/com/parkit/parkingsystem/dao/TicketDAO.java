package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.parkit.parkingsystem.config.DataBaseConfig.setDatabaseValues;


/**
 * Manage a ticket related to a parking.
 */
public class TicketDAO {
    /**
     * The Data base configuration.
     */
    private final DataBaseConfig dataBaseConfig;

    /**
     * Instantiates a new Ticket dao.
     *
     * @param pDataBaseConfig the data base configuration
     */
    public TicketDAO(final DataBaseConfig pDataBaseConfig) {
        this.dataBaseConfig = pDataBaseConfig;
    }

    /**
     * Check if the user is a recurrent one.
     *
     * @param vehicleRegNumber the vehicle registration number
     * @return if is a recurrent user or not
     */
    public boolean checkRecurrentUser(final String vehicleRegNumber) {
        Connection con;
        int userNumberRecurrences = 0;

        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.GET_USER_RECURRENCES);
            Object[] databaseValues = {vehicleRegNumber};
            setDatabaseValues(ps, databaseValues);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                userNumberRecurrences = resultSet.getInt(
                        DBConstants.COUNT_AVAILABLE_PARKING_COLUMN_LABEL);
            }
            dataBaseConfig.closeResultSet(resultSet);
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }

        return userNumberRecurrences > 0;
    }

    /**
     * Save a ticket.
     *
     * @param ticket the ticket to save
     */
    public void saveTicket(final Ticket ticket) {
        Connection con;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.SAVE_TICKET);
            Object[] databaseValues = {
                    ticket.getParkingSpot().getId(),
                    ticket.getVehicleRegNumber(),
                    ticket.getPrice(),
                    Timestamp.valueOf(ticket.getInTime()),
                    (ticket.getOutTime() == null)
                            ? null : (Timestamp.valueOf(ticket.getOutTime()))
            };
            setDatabaseValues(ps, databaseValues);
            ps.execute();
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * Gets a ticket.
     *
     * @param vehicleRegNumber the vehicle registration number
     * @return the ticket
     */
    public Ticket getTicket(final String vehicleRegNumber) {
        Connection con;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            Object[] databaseValues = {vehicleRegNumber};
            setDatabaseValues(ps, databaseValues);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(
                        rs.getInt(
                                DBConstants.TICKET_PARKING_NUMBER_COLUMN_LABEL),
                        ParkingType.valueOf(
                                rs.getString(
                                        DBConstants.PARKING_TYPE_COLUMN_LABEL)),
                        false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(DBConstants.TICKET_ID_COLUMN_LABEL));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(
                        DBConstants.TICKET_PRICE_COLUMN_LABEL));
                ticket.setInTime(rs.getTimestamp(
                        DBConstants.TICKET_IN_TIME_COLUMN_LABEL)
                        .toLocalDateTime());
                final Timestamp outTime
                        = rs.getTimestamp(
                        DBConstants.TICKET_OUT_TIME_COLUMN_LABEL);
                if (outTime != null) {
                    ticket.setOutTime(outTime.toLocalDateTime());
                }
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
        return ticket;
    }

    /**
     * Update a ticket.
     *
     * @param ticket the ticket to update
     * @return if the ticket is updated or not
     */
    public boolean updateTicket(final Ticket ticket) {
        Connection con;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps =
                    con.prepareStatement(DBConstants.UPDATE_TICKET);
            Object[] databaseValues = {
                    ticket.getPrice(),
                    Timestamp.valueOf(ticket.getOutTime()),
                    ticket.getId(),
            };
            setDatabaseValues(ps, databaseValues);
            ps.execute();
            dataBaseConfig.closePreparedStatement(ps);
            dataBaseConfig.closeConnection(con);
            return true;
        } catch (SQLException | ClassNotFoundException throwable) {
            throwable.printStackTrace();
        }
        return false;
    }
}
