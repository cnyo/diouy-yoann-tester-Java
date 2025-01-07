package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    public static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    public void setUpPerTest() {
        ticket = new Ticket();
    }

    @Test
    @DisplayName("How to calculate the price of a car")
    public void calculateFareCarTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    @DisplayName("How to calculate the price of a bike")
    public void calculateFareBikeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(Fare.BIKE_RATE_PER_HOUR, ticket.getPrice());
    }

    @Test
    @DisplayName("Throw an exception when parkingType is null")
    public void calculateFareUnkownTypeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, null,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(
                NullPointerException.class,
                () -> fareCalculatorService.calculateFare(ticket)
        );
    }

    @Test
    @DisplayName("Trigger an exception when the entry time is greater than the exit time")
    public void calculateFareBikeWithInTimeUpperToOutTimeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() + (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        assertThrows(
                IllegalArgumentException.class,
                () -> fareCalculatorService.calculateFare(ticket)
        );
    }

    @Test
    @DisplayName("Calculate the price of parking a motorbike for less than an hour")
    public void calculateFareBikeWithLessThanOneHourParkingTimeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice() );
    }

    @Test
    @DisplayName("Calculating the price of parking a car for less than an hour")
    public void calculateFareCarWithLessThanOneHourParkingTimeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  45 * 60 * 1000) );//45 minutes parking time should give 3/4th parking fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(
                fareCalculatorService.roundPrice(0.75 * Fare.CAR_RATE_PER_HOUR),
                ticket.getPrice()
        );
    }

    @Test
    @DisplayName("Calculating the price of parking a car for more than one day")
    public void calculateFareCarWithMoreThanADayParkingTimeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (24 * 60 * 60 * 1000) );//24 hours parking time should give 24 * parking fare per hour
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals( (24 * Fare.CAR_RATE_PER_HOUR) , ticket.getPrice());
    }

    @Test
    @DisplayName("Successfully returning free travel to a car for less than 30 minutes")
    public void calculateFareCarWithLessThan30minutesParkingTimeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (20 * 60 * 1000) );//20 minutes parking time should give free fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(0 , ticket.getPrice());
    }

    @Test
    @DisplayName("Successfully returning free bike travel for less than 30 minutes")
    public void calculateFareBikeWithLessThan30minutesParkingTimeTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (  20 * 60 * 1000) );//20 minutes parking time should give free fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket);

        assertEquals(0 , ticket.getPrice());
    }

    @Test
    @DisplayName("How to calculate the discount price for a car")
    public void calculateFareCarWithDiscountTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket, true);

        assertEquals(
                fareCalculatorService.roundPrice((1 * Fare.CAR_RATE_PER_HOUR) - ((1 * Fare.CAR_RATE_PER_HOUR) * ((double) 5 / 100))),
                ticket.getPrice()
        );
    }

    @Test
    @DisplayName("How to calculate the discount price for a bike")
    public void calculateFareBikeWithDiscountTest(){
        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - (60 * 60 * 1000) );//20 minutes parking time should give free fare
        Date outTime = new Date();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE,false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);

        fareCalculatorService.calculateFare(ticket, true);

        assertEquals(
                (1 * Fare.BIKE_RATE_PER_HOUR) - (((double) 5 / 100)),
                ticket.getPrice()
        );
    }

    @Test
    @DisplayName("Successful rounding if the hundredth is greater than 5")
    public void roundPriceHalfUpTest(){
        double roundedPrice = fareCalculatorService.roundPrice(1.42526688);

        assertEquals(1.43, roundedPrice);
    }

    @Test
    @DisplayName("Successful rounding if one hundredth equals 5")
    public void roundPriceUpTest(){
        double roundedPrice = fareCalculatorService.roundPrice(1.42626688);

        assertEquals(1.43, roundedPrice);
    }

    @Test
    @DisplayName("Successful rounding if the hundredth is less than 5")
    public void roundPriceDownTest(){
        double roundedPrice = fareCalculatorService.roundPrice(1.42426688);

        assertEquals(1.42, roundedPrice);
    }
}
