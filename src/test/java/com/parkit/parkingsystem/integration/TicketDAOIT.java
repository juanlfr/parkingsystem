package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class TicketDAOIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;
	private static Ticket ticket;
	private static ParkingSpot parkingSpot;

	@BeforeAll
	private static void setUp() throws Exception {

		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
		ticket = new Ticket();
		parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
		// set ticket info
		ticket.setId(1);
		ticket.setInTime(new Date());
		ticket.setOutTime(null);
		ticket.setVehicleRegNumber("TestIT123");
		ticket.setPrice(0);
		ticket.setParkingSpot(parkingSpot);
		// clear DB
		dataBasePrepareService.clearDataBaseEntries();

	}

	@AfterAll
	private static void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	@Order(1)
	public void saveTicketTest() {

		boolean returnedValue = ticketDAO.saveTicket(ticket);
		assertThat(returnedValue).isFalse();

	}

	@Test
	@Order(2)
	public void getTicketTest() {

		Ticket returnedTicket = ticketDAO.getTicket(ticket.getVehicleRegNumber());

		assertThat(returnedTicket.getId()).isEqualTo(1);
		assertThat(returnedTicket.getOutTime()).isNull();
		assertThat(returnedTicket.getVehicleRegNumber()).isEqualTo("TestIT123");
	}

	@Test
	@Order(3)
	public void updateTicketTest() {

		Date currentDate = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(currentDate);
		c.add(Calendar.DATE, 1);

		ticket.setPrice(20.5);
		ticket.setOutTime(c.getTime());
		boolean returnedValue = ticketDAO.updateTicket(ticket);

		assertThat(ticket.getVehicleRegNumber()).isEqualTo("TestIT123");
		assertThat(returnedValue).isTrue();

	}

}
