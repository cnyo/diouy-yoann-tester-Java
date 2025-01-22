package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareCalculatorService {

    private static final int DISCOUNT_PERCENT = 5;
    private static final double DIGITAL_HOUR_FREE = 0.50;

    public void calculateFare(Ticket ticket, boolean discount){

        if (!isValidDate(ticket)) {
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inTime = (long) ticket.getInTime().getTime();
        long outTime = (long) ticket.getOutTime().getTime();

        long timeDuration = outTime - inTime;
        double decimalHours = timeDuration/3600000.0;

        if (decimalHours < DIGITAL_HOUR_FREE) {
            ticket.setPrice(0);

            return;
        }

        double roundedPrice = 0.00;

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                roundedPrice = roundPrice(decimalHours * Fare.CAR_RATE_PER_HOUR);
                ticket.setPrice(roundedPrice);
                break;
            }
            case BIKE: {
                roundedPrice = roundPrice(decimalHours * Fare.BIKE_RATE_PER_HOUR);
                ticket.setPrice(roundedPrice);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }

        if (discount){
            roundedPrice = roundPrice(ticket.getPrice() - (ticket.getPrice() * ((double) DISCOUNT_PERCENT / 100)));
            ticket.setPrice(roundedPrice);
        }
    }

    public void calculateFare(Ticket ticket){
        calculateFare(ticket, false);
    }

    private boolean isValidDate(Ticket ticket) {
        if( (ticket.getInTime() == null) || (ticket.getOutTime() == null) ){
            return false;
        }

        return !ticket.getOutTime().before(ticket.getInTime());
    }

    public double roundPrice(double price) {
        BigDecimal bd = new BigDecimal(price);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }
}