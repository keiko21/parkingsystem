package com.parkit.parkingsystem.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
    public static final String VEHICLE_REG_NUMBER_ABCD = "ABCD";
    public static final String EMPTY_STRING = " ";
    private final InputStream inputStream = System.in;
    private InputReaderUtil inputReaderUtil;

    @AfterEach
    public void tearDown() {
        System.setIn(inputStream);
    }

    @ParameterizedTest(name = "Option {0}")
    @CsvSource({"1,1", "2,2", "3,3"})
    void readSelection(String optionString, int optionInt) {
        createInputReaderUtil(optionString);

        assertThat(inputReaderUtil.readSelection()).isEqualTo(optionInt);
    }

    @Test
    void readVehicleRegistrationNumberABCD() {
        createInputReaderUtil(VEHICLE_REG_NUMBER_ABCD);

        assertThat(inputReaderUtil.readVehicleRegistrationNumber()).isEqualTo(VEHICLE_REG_NUMBER_ABCD);
    }

    @Test
    void readVehicleRegistrationNumberEmptyString() {
        createInputReaderUtil(EMPTY_STRING);

        assertThatIllegalArgumentException().isThrownBy(() -> inputReaderUtil.readVehicleRegistrationNumber());
    }

    private void createInputReaderUtil(String optionString) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(optionString.getBytes(StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(byteArrayInputStream);
        inputReaderUtil = new InputReaderUtil(scanner);
    }
}