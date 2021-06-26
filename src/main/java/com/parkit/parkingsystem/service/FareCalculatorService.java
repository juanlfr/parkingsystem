package com.parkit.parkingsystem.service;

import java.util.concurrent.TimeUnit;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

	private TicketDAO ticketDAO;

	public FareCalculatorService() {
	}

	public FareCalculatorService(TicketDAO ticketDAO) {
		this.ticketDAO = ticketDAO;
	}

	public void calculateFare(Ticket ticket) {

		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double durationInHours = calculateDuration(ticket);

		ParkingType parkingType = ticket.getParkingSpot().getParkingType();

		if (durationInHours >= 0.5) {

			switch (parkingType) {

			case CAR: {
				ticket.setPrice(durationInHours * Fare.CAR_RATE_PER_HOUR);
				break;
			}
			case BIKE: {
				ticket.setPrice(durationInHours * Fare.BIKE_RATE_PER_HOUR);
				break;
			}
			default:
				throw new IllegalArgumentException("Unkown Parking Type");
			}
		} else {
			ticket.setPrice(0);
		}
		//verify if the user has come before to the parcking to apply a disocunt
		if (isRecurrentUser(ticket) > 1 && durationInHours >= 0.5) {
			calculateFareWithDiscount(ticket);
		}

	}

	private double calculateDuration(Ticket ticket) {

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		// Duration calculation in milliseconds
		long durationInMillies = Math.abs(outHour - inHour);
		// Conversion from milliseconds to hours
		double durationInHours = (double) TimeUnit.MILLISECONDS.toMinutes(durationInMillies) / 60;
		return durationInHours;
	}

	public int isRecurrentUser(Ticket ticket) {
		int numberOfUserEntries = ticketDAO.getCountUserVisits(ticket.getVehicleRegNumber());
		return numberOfUserEntries;

	}

	private void calculateFareWithDiscount(Ticket ticket) {
		double ticketPriceWithDiscount = ticket.getPrice() - ticket.getPrice() * Fare.DISCOUNT_PERCENT;
		ticket.setPrice(ticketPriceWithDiscount);
	}

}