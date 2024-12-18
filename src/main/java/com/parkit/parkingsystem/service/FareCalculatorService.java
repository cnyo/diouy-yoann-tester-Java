package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

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

        switch (ticket.getParkingSpot().getParkingType()){
            case CAR: {
                ticket.setPrice(decimalHours * Fare.CAR_RATE_PER_HOUR);
                break;
            }
            case BIKE: {
                ticket.setPrice(decimalHours * Fare.BIKE_RATE_PER_HOUR);
                break;
            }
            default: throw new IllegalArgumentException("Unkown Parking Type");
        }

        if (discount){
            ticket.setPrice(ticket.getPrice() - (ticket.getPrice() * ((double) 5 / 100)));
        }
    }

    public void calculateFare(Ticket ticket){
        calculateFare(ticket, false);
    }
}