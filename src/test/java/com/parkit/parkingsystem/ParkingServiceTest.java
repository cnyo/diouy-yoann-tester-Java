package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LoggingExtension.class)
public class ParkingServiceTest {

    private static ParkingService parkingService;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    @Mock
    private static TicketDAO ticketDAO;

    public static void setLogger(Logger customLogger) {
    }

    @BeforeEach
    public void setUpPerTest() {
        try {
            parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Successfully handling the entry of a new car")
    public void processIncomingVehicleCarTest() {
        try {
            when(ticketDAO.getNbTicket(anyString())).thenReturn(0);
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

            parkingService.processIncomingVehicle();

            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Successfully handling the entrance to a car that has already been here")
    public void processIncomingVehicleDiscountCarTest() {
        try {
            when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
            when(inputReaderUtil.readSelection()).thenReturn(1);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

            parkingService.processIncomingVehicle();

            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Successfully treating the entrance to a bike")
    public void processIncomingVehicleBikeTest() {
        try {
            when(ticketDAO.getNbTicket(anyString())).thenReturn(1);
            when(inputReaderUtil.readSelection()).thenReturn(2);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);

            parkingService.processIncomingVehicle();

            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Successfully handling the entry of a bike that has already been here")
    public void processIncomingVehicleDiscountBikeTest() {
        try {
            when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
            when(inputReaderUtil.readSelection()).thenReturn(2);
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);

            parkingService.processIncomingVehicle();

            verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Successfully throw exception if the registration number is not text")
    public void readVehicleRegistrationNumberExceptionTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenThrow(IllegalArgumentException.class);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);

        assertThrows(IllegalArgumentException.class, () -> inputReaderUtil.readVehicleRegistrationNumber());
        parkingService.processIncomingVehicle();
    }

    @Test
    @DisplayName("Successfully handling the exit of a vehicle without a discount price")
    public void processExitingVehicleWithoutDiscountTest(){
        try {
            Ticket ticket = newTicketForTest();

            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            when(ticketDAO.getNbTicket(anyString())).thenReturn(1);

            parkingService.processExitingVehicle();

            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Successfully handling the exit of a vehicle at a discount price")
    public void processExitingVehicleWithDiscountTest() {
        try {
            Ticket ticket = newTicketForTest();

            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
            when(ticketDAO.getNbTicket(anyString())).thenReturn(2);

            parkingService.processExitingVehicle();

            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    @Test
    @DisplayName("Successfully to retrieve an available parking space for a car")
    public void getNextParkingNumberIfAvailableForCarReturnTrue(){
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertThat(parkingSpot.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Failing to retrieve an available parking space for a car")
    public void getNextParkingNumberIfAvailableForCarReturnNull(){
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);
        when(inputReaderUtil.readSelection()).thenReturn(1);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertThat(parkingSpot).isNull();
    }

    @Test
    @DisplayName("Failing to retrieve an available parking space for a bike")
    public void getNextParkingNumberIfAvailableForBikeReturnTrue(){
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(1);
        when(inputReaderUtil.readSelection()).thenReturn(2);

        ParkingSpot parkingSpot = parkingService.getNextParkingNumberIfAvailable();
        assertThat(parkingSpot.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("Logging parsing error message on incorrect selection")
    public void processIncomingVehicleBadSelectionTest() {
        TestAppender testAppender = new TestAppender("TestAppender");
        testAppender.start();
        LoggerConfig loggerConfig = Configurator.initialize(null, "log4j2.xml")
                .getConfiguration()
                .getRootLogger();
        loggerConfig.addAppender(testAppender, null, null);
        String expectedMessage = "Error parsing user input for type of vehicle";

        when(inputReaderUtil.readSelection()).thenReturn(0);

        parkingService.processIncomingVehicle();

        List<String> logMessages = testAppender.getMessages();
        assertThat(logMessages.contains(expectedMessage)).isTrue();

        loggerConfig.removeAppender(testAppender.getName());
    }

    @Test
    @DisplayName("Recovering the type of vehicle")
    public void getVehicleTypeTest() {
        try {
            Ticket ticket = newTicketForTest();
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.getNbTicket(anyString())).thenReturn(2);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);

            parkingService.processExitingVehicle();

            verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        } catch (Exception e) {
            e.printStackTrace();
            throw  new RuntimeException("Failed to set up test mock objects");
        }
    }

    private Ticket newTicketForTest() {
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);
        Ticket ticket = new Ticket();
        ticket.setInTime(new Date(System.currentTimeMillis() - (60*60*1000)));
        ticket.setParkingSpot(parkingSpot);
        ticket.setVehicleRegNumber("ABCDEF");

        return ticket;
    }
}
