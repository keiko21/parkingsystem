package com.parkit.parkingsystem.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.parkit.parkingsystem.constants.DBConstants.GET_NEXT_PARKING_SPOT;
import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@ExtendWith(MockitoExtension.class)
class DataBaseConfigTest {
    private static DataBaseConfig dataBaseConfig;

    private static PreparedStatement preparedStatement;

    private static Connection connection;

    @BeforeAll
    static void setUp() throws SQLException, ClassNotFoundException {
        dataBaseConfig = new DataBaseConfig();
        connection = dataBaseConfig.getConnection();
        preparedStatement = connection.prepareStatement(GET_NEXT_PARKING_SPOT);
        //preparedStatement.setObject(1, DBConstants.GET_NEXT_PARKING_SPOT);
        //when(connection.prepareStatement(anyString())).thenReturn
        // (preparedStatement);
    }

    @BeforeEach
    void setUpPerTest() {

    }

    @Test
    void setDatabaseValues() throws SQLException {
        Object[] databaseValues = {"CAR"};
        DataBaseConfig.setDatabaseValues(preparedStatement, databaseValues);
        assertThat(preparedStatement.toString()).isEqualTo("");
    }

}