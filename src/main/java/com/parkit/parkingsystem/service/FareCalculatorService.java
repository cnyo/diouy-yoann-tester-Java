package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket, boolean discount){
        if( (ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime())) ){
            throw new IllegalArgumentException("Out time provided is incorrect:"+ticket.getOutTime().toString());
        }

        long inHour = (long) ticket.getInTime().getTime();
        long outHour = (long) ticket.getOutTime().getTime();

        long timeDuration = outHour - inHour;
        double decimalHours = timeDuration/3600000.0;

        if (decimalHours < 0.50) {
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
            ticket.setPrice(ticket.getPrice() - (ticket.getPrice() * ((double) 5 / 100)));
            roundedPrice = roundPrice(ticket.getPrice() - (ticket.getPrice() * ((double) 5 / 100)));
            ticket.setPrice(roundedPrice);
        }
    }

    public void calculateFare(Ticket ticket){
        calculateFare(ticket, false);
    }

    public double roundPrice(double price) {
        BigDecimal bd = new BigDecimal(price);
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        return bd.doubleValue();
    }
}