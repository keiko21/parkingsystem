package com.parkit.parkingsystem.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InputReaderUtilTest {
    public static final String OPTION_ONE_STRING = "1";
    public static final int OPTION_ONE = 1;
    public static final String VEHICLE_REG_NUMBER_ABCD = "ABCD";
    public static final String EMPTY_STRING = " ";
    private final InputStream inputStream = System.in;
    private ByteArrayInputStream byteArrayInputStream;
    private InputReaderUtil inputReaderUtil;
    private Scanner scanner;

    @AfterEach
    public void tearDown() {
        System.setIn(inputStream);
    }

    @Test
    void readSelectionWithOptionOne() {
        byteArrayInputStream = new ByteArrayInputStream(OPTION_ONE_STRING.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(byteArrayInputStream);
        inputReaderUtil = new InputReaderUtil(scanner);

        assertThat(inputReaderUtil.readSelection()).isEqualTo(OPTION_ONE);
    }

    @Test
    void readVehicleRegistrationNumberABCD() {
        byteArrayInputStream = new ByteArrayInputStream(VEHICLE_REG_NUMBER_ABCD.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(byteArrayInputStream);
        inputReaderUtil = new InputReaderUtil(scanner);

        assertThat(inputReaderUtil.readVehicleRegistrationNumber()).isEqualTo(VEHICLE_REG_NUMBER_ABCD);
    }

    @Test
    void readVehicleRegistrationNumberNull() {
        byteArrayInputStream = new ByteArrayInputStream(EMPTY_STRING.getBytes(StandardCharsets.UTF_8));
        scanner = new Scanner(byteArrayInputStream);
        inputReaderUtil = new InputReaderUtil(scanner);

        assertThatIllegalArgumentException().isThrownBy(() -> inputReaderUtil.readVehicleRegistrationNumber());
    }
}