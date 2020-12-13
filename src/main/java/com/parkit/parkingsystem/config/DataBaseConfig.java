package com.parkit.parkingsystem.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * «
 * Database configurator.
 */
public class DataBaseConfig {
    /**
     * Logger of database configuration.
     *
     * @see Logger
     */
    private static final Logger LOGGER =
            LogManager.getLogger("DataBaseConfig");

    /**
     * Properties path of the database.
     */
    private static final String PROPERTIES_PATH =
            "src/main/resources/database.properties";
    /**
     * The Url from properties file.
     */
    private String url;
    /**
     * The User from properties file.
     */
    private String user;
    /**
     * The Password from properties file.
     */
    private String password;

    /**
     * Sets database values.
     *
     * @param preparedStatement the prepared statement
     * @param values            the values
     * @throws SQLException the sql exception
     */
    public static void setDatabaseValues(
            final PreparedStatement preparedStatement,
            final Object... values) throws SQLException {
        for (byte parameterIndex = 0;
             parameterIndex < values.length;
             parameterIndex++) {
            preparedStatement
                    .setObject(parameterIndex + 1, values[parameterIndex]);
        }
    }

    /**
     * Gets connection to database.
     *
     * @return the connection to database
     * @throws ClassNotFoundException the class not found exception
     * @throws SQLException           the sql exception
     */
    public Connection getConnection()
            throws ClassNotFoundException, SQLException {
        LOGGER.info("Create DB connection");
        Class.forName("com.mysql.cj.jdbc.Driver");

        try (FileInputStream fileInputStream =
                     new FileInputStream(PROPERTIES_PATH)) {
            Properties properties = new Properties();
            properties.load(fileInputStream);

            url = properties.getProperty("url");
            user = properties.getProperty("user");
            password = properties.getProperty("password");

        } catch (IOException e) {
            LOGGER.error("Cannot write into" + PROPERTIES_PATH, e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Close database connection.
     *
     * @param con the connection
     */
    public void closeConnection(final Connection con) {
        if (con != null) {
            try {
                con.close();
                LOGGER.info("Closing DB connection");
            } catch (SQLException e) {
                LOGGER.error("Error while closing connection", e);
            }
        }
    }

    /**
     * Close prepared statement.
     *
     * @param ps the prepared statement
     */
    public void closePreparedStatement(final PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
                LOGGER.info("Closing Prepared Statement");
            } catch (SQLException e) {
                LOGGER.error("Error while closing prepared statement", e);
            }
        }
    }

    /**
     * Close result set.
     *
     * @param rs the result set
     */
    public void closeResultSet(final ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                LOGGER.info("Closing Result Set");
            } catch (SQLException e) {
                LOGGER.error("Error while closing result set", e);
            }
        }
    }

}
