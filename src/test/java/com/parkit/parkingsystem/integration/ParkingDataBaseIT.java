package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ParkingDataBaseIT {

    private static final DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private static FareCalculatorService fareCalculatorService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        dataBasePrepareService.clearDataBaseEntries();
    }

    @Test
    @DisplayName("Check that a ticket is actualy saved in DB and Parking table is updated with availability")
    public void testParkingACar(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();

        Ticket ticket = ticketDAO.getTicket("ABCDEF");

        assertThat(ticket).isNotNull();
        assertThat(ticket.getParkingSpot().isAvailable()).isFalse();
    }

    @Test
    @DisplayName("Check that the fare generated and out time are populated correctly in the database")
    public void testParkingLotExit(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();

        // Set outTime et calculate price to 1 hour
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        long outIme = (System.currentTimeMillis() + (60 * 60 * 1000));
        Date outDate = new Date(outIme);
        ticket.setOutTime(outDate);

        fareCalculatorService.calculateFare(ticket);
        ticketDAO.updateTicket(ticket);

        assertThat(ticket.getPrice()).isNotEqualTo(0.0);
        assertThat(ticket.getOutTime()).isNotNull();
    }

    @Test
    @DisplayName("Check the calculation of the price of a ticket in the case of a recurring user")
    public void testParkingLotExitRecurringUser(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        parkingService.processExitingVehicle();

        // Set outTime et calculate price to 1 hour
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        long outIme = (System.currentTimeMillis() + (60 * 60 * 1000));
        Date outDate = new Date(outIme);
        ticket.setOutTime(outDate);

        fareCalculatorService.calculateFare(ticket);
        double withoutDiscountPrice = ticket.getPrice();
        fareCalculatorService.calculateFare(ticket, true);
        double discountPrice = ticket.getPrice();

        ticketDAO.updateTicket(ticket);

        assertThat(withoutDiscountPrice).isGreaterThan(discountPrice);
    }

}
