package com.parkit.parkingsystem.constants;

/**
 * Contains all constants relative to database communication.
 */
public final class DBConstants {
    /**
     * Get next parking spot from database.
     */
    public static final String GET_NEXT_PARKING_SPOT =
            "select min(PARKING_NUMBER) "
                    + " from parking where AVAILABLE = true and TYPE = ?";
    /**
     * Update a parking spot into database.
     */
    public static final String UPDATE_PARKING_SPOT =
            "update parking set available = ? where PARKING_NUMBER = ?";
    /**
     * Save a ticket into database.
     */
    public static final String SAVE_TICKET =
            "insert into ticket(PARKING_NUMBER, "
                    + "VEHICLE_REG_NUMBER, "
                    + "PRICE, "
                    + "IN_TIME, "
                    + "OUT_TIME) "
                    + "values(?,?,?,?,?)";
    /**
     * Update a ticket into database.
     */
    public static final String UPDATE_TICKET =
            "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    /**
     * Get a ticket from database.
     */
    public static final String GET_TICKET =
            "select "
                    + "t.PARKING_NUMBER, "
                    + "t.ID, "
                    + "t.PRICE, "
                    + "t.IN_TIME, "
                    + "t.OUT_TIME, "
                    + "p.TYPE, "
                    + "p.AVAILABLE "
                    + "from ticket "
                    + "t,parking p where p.parking_number = t.parking_number "
                    + "and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";
    /**
     * Get user recurrences from database.
     */
    public static final String GET_USER_RECURRENCES =
            "select count(p.AVAILABLE) from ticket t,parking p "
                    + "where p.parking_number = t.parking_number "
                    + "and t.VEHICLE_REG_NUMBER=?";
    /**
     * Count available parking column label constant.
     */
    public static final String COUNT_AVAILABLE_PARKING_COLUMN_LABEL
            = "count(p.AVAILABLE)";
    /**
     * Ticket parking number column label constant.
     */
    public static final String TICKET_PARKING_NUMBER_COLUMN_LABEL
            = "t.PARKING_NUMBER";
    /**
     * Parking type column label constant.
     */
    public static final String PARKING_TYPE_COLUMN_LABEL = "p.TYPE";
    /**
     * Ticket ID column label constant.
     */
    public static final String TICKET_ID_COLUMN_LABEL = "t.ID";
    /**
     * Ticket price column label constant.
     */
    public static final String TICKET_PRICE_COLUMN_LABEL = "t.PRICE";
    /**
     * Ticket in time column label constant.
     */
    public static final String TICKET_IN_TIME_COLUMN_LABEL = "t.IN_TIME";
    /**
     * Ticket out time column label constant.
     */
    public static final String TICKET_OUT_TIME_COLUMN_LABEL = "t.OUT_TIME";
    /**
     * Min parking number column label.
     */
    public static final String MIN_PARKING_NUMBER_COLUMN_LABEL
            = "min(PARKING_NUMBER)";

    /**
     * Instantiates a new Db constants.
     */
    private DBConstants() {
    }
}
