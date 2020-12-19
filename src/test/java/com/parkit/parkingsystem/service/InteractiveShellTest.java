package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractiveShellTest {
    public static final int OPTION_ONE = 1;
    public static final int OPTION_TWO = 2;
    public static final int OPTION_THREE = 3;
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    @Mock
    private ParkingService parkingService;
    @Mock
    private InputReaderUtil inputReaderUtil;
    private InteractiveShell interactiveShell;

    @BeforeEach
    public void setUpPerTest() {
        System.setOut(new PrintStream(outputStreamCaptor));
        interactiveShell = new InteractiveShell(parkingService);
        when(parkingService.getInputReaderUtil()).thenReturn(inputReaderUtil);
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void loadInterfaceWithOptionOneAndOptionThreeToCloseIt() {
        Mockito.doNothing().when(parkingService).processIncomingVehicle();
        when(inputReaderUtil.readSelection()).thenReturn(OPTION_ONE, OPTION_THREE);

        interactiveShell.loadInterface();

        assertThat(outputStreamCaptor.toString().trim()).containsOnlyOnce(
                "Welcome to Parking System!");
        assertThat(outputStreamCaptor.toString().trim()).contains(
                "Please select an option. Simply enter the number to choose an action",
                "1 New Vehicle Entering - Allocate Parking Space",
                "2 Vehicle Exiting - Generate Ticket Price",
                "3 Shutdown System");
        verify(parkingService, times(1)).processIncomingVehicle();
        assertThat(outputStreamCaptor.toString().trim()).containsOnlyOnce(
                "Exiting from the system!");
    }

    @Test
    void loadInterfaceWithOptionTwoAndOptionThreeToCloseIt() {
        Mockito.doNothing().when(parkingService).processExitingVehicle();
        when(inputReaderUtil.readSelection()).thenReturn(OPTION_TWO, OPTION_THREE);

        interactiveShell.loadInterface();

        assertThat(outputStreamCaptor.toString().trim()).containsOnlyOnce(
                "Welcome to Parking System!");
        assertThat(outputStreamCaptor.toString().trim()).contains(
                "Please select an option. Simply enter the number to choose an action",
                "1 New Vehicle Entering - Allocate Parking Space",
                "2 Vehicle Exiting - Generate Ticket Price",
                "3 Shutdown System");
        verify(parkingService, times(1)).processExitingVehicle();
        assertThat(outputStreamCaptor.toString().trim()).containsOnlyOnce(
                "Exiting from the system!");
    }

    @Test
    void loadInterfaceWithOptionThree() {
        doReturn(OPTION_THREE).when(inputReaderUtil).readSelection();

        interactiveShell.loadInterface();

        assertThat(outputStreamCaptor.toString().trim()).containsOnlyOnce(
                "Welcome to Parking System!");
        assertThat(outputStreamCaptor.toString().trim()).contains(
                "Please select an option. Simply enter the number to choose an action",
                "1 New Vehicle Entering - Allocate Parking Space",
                "2 Vehicle Exiting - Generate Ticket Price",
                "3 Shutdown System");
        assertThat(outputStreamCaptor.toString().trim()).containsOnlyOnce(
                "Exiting from the system!");
    }
}