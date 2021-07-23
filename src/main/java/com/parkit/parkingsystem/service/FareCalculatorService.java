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

	/**
	 * Method to calculate the price of a ticket based on duration and number of
	 * user visits
	 * 
	 * @param ticket
	 */
	public void calculateFare(Ticket ticket) {

		if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
			throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
		}

		double durationInHours = calculateDuration(ticket);

		ParkingType parkingType = ticket.getParkingSpot().getParkingType();

		// verify if the user has stayed in the parking for more than half hour
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
			// verify if the user has come before more than once to the parking to apply a
			// discount
			if (numberOfUserVisits(ticket) > Fare.NUMBER_OF_USER_VISITS) {

				double ticketPriceWithDiscount = ticket.getPrice() - ticket.getPrice() * Fare.DISCOUNT_PERCENT;

				ticket.setPrice(ticketPriceWithDiscount);
			}
		} else {
			ticket.setPrice(0);
		}
	}

	/**
	 * Method to calculate the duration time
	 * 
	 * @param ticket
	 * @return duration time in hours
	 */

	private double calculateDuration(Ticket ticket) {

		long inHour = ticket.getInTime().getTime();
		long outHour = ticket.getOutTime().getTime();
		// Duration calculation in milliseconds
		long durationInMillies = Math.abs(outHour - inHour);
		// Conversion from milliseconds to hours
		double durationInHours = (double) TimeUnit.MILLISECONDS.toMinutes(durationInMillies) / 60;
		return durationInHours;
	}

	/**
	 * Method to get the number of user's visits
	 * 
	 * @param ticket
	 * @return
	 */
	public int numberOfUserVisits(Ticket ticket) {
		int numberOfUserEntries = ticketDAO.getCountUserVisits(ticket.getVehicleRegNumber());
		return numberOfUserEntries;
	}

}