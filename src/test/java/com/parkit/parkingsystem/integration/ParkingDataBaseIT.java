package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
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

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    public static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
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
        Ticket ticket = ticketDAO.getTicket("ABCDEF");
        // 60 minutes parking time should give 3/4th parking fare
        long inIme = (System.currentTimeMillis() - (60 * 60 * 1000));
        Date inDate = new Date(inIme);
        ticket.setInTime(inDate);
        dataBasePrepareService.updateTicket(ticket);
        parkingService.processExitingVehicle();

        ticket = ticketDAO.getTicket("ABCDEF");

        assertThat(ticket.getPrice()).isNotEqualTo(0.0);
        assertThat(ticket.getOutTime()).isNotNull();
    }

    @Test
    @DisplayName("Check the calculation of the price of a ticket in the case of a recurring user")
    public void testParkingLotExitRecurringUser(){
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // First entrance
        parkingService.processIncomingVehicle();
        Ticket firstTicket = ticketDAO.getTicket("ABCDEF");
        // 120 minutes parking time should give 3/4th parking fare
        Date inDate = new Date((System.currentTimeMillis() - (60 * 60 * 1000)));
        firstTicket.setInTime(inDate);
        dataBasePrepareService.updateTicket(firstTicket);

        parkingService.processExitingVehicle();
        firstTicket = ticketDAO.getTicket("ABCDEF");

        // 2nd entrance
        parkingService.processIncomingVehicle();
        Ticket secondTicket = ticketDAO.getTicket("ABCDEF");
        // 60 minutes parking time should give 3/4th parking fare
        inDate = new Date((System.currentTimeMillis() - (60 * 60 * 1000)));
        secondTicket.setInTime(inDate);
        dataBasePrepareService.updateTicket(secondTicket);
        parkingService.processExitingVehicle();
        secondTicket = ticketDAO.getTicket("ABCDEF");

        assertThat(firstTicket.getPrice()).isGreaterThan(secondTicket.getPrice());
    }

}
