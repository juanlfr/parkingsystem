package com.parkit.parkingsystem.service;

import java.util.concurrent.TimeUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	public void calculateFare(Ticket ticket) {
		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		// Duration calculation in milliseconds
		long durationInMillies = Math.abs(outHour - inHour);
		// Conversion from milliseconds to hours
		double durationInHours = (double) TimeUnit.MILLISECONDS.toMinutes(durationInMillies) / 60;

		ParkingType parkingType = ticket.getParkingSpot().getParkingType();

		if (durationInHours > 0.5) {
			switch (parkingType) {

			case CAR: {
				ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
				if (ticket.getOcurrenciesNumber() > 1) {
					ticket.setPrice(ticket.getPrice() - ticket.getPrice() * 0.05);
				}
				break;
			}
			case BIKE: {
				ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
				if (ticket.getOcurrenciesNumber() > 1) {
					ticket.setPrice(ticket.getPrice() - ticket.getPrice() * 0.05);
				}
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {
			ticket.setPrice(0);
		}
	}
}