package com.parkit.parkingsystem.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

	private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
	private static ParkingSpotDAO parkingSpotDAO;
	private static TicketDAO ticketDAO;
	private static DataBasePrepareService dataBasePrepareService;

	@Mock
	private static InputReaderUtil inputReaderUtil;

	@BeforeAll
	private static void setUp() throws Exception {
		parkingSpotDAO = new ParkingSpotDAO();
		parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
		ticketDAO = new TicketDAO();
		ticketDAO.dataBaseConfig = dataBaseTestConfig;
		dataBasePrepareService = new DataBasePrepareService();
	}

	@BeforeEach
	private void setUpPerTest() throws Exception {

		when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
		// dataBasePrepareService.clearDataBaseEntries();
	}

	@AfterAll
	private static void tearDown() {
		dataBasePrepareService.clearDataBaseEntries();
	}

	@Test
	@org.junit.jupiter.api.Order(1)
	public void testParkingACar() {

		when(inputReaderUtil.readSelection()).thenReturn(1);

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

		parkingService.processIncomingVehicle();
		// TODO: check that a ticket is actually saved in DB and Parking table is
		// updated with availability
		Ticket ticketInBDD = null;
		try {
			ticketInBDD = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
		} catch (Exception e) {

			e.printStackTrace();
		}
		assertThat(ticketInBDD).hasFieldOrPropertyWithValue("vehicleRegNumber", "ABCDEF");
		assertThat(ticketInBDD.getParkingSpot()).hasFieldOrPropertyWithValue("isAvailable", false);

	}

	@Test
	@org.junit.jupiter.api.Order(2)
	public void testParkingLotExit() {
		// testParkingACar(); => Not good for isolations of the test

		ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		parkingService.processExitingVehicle();

		Ticket ticketInBDD = null;
		try {
			ticketInBDD = ticketDAO.getTicket(inputReaderUtil.readVehicleRegistrationNumber());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertThat(ticketInBDD.getPrice()).isNotNull();
		assertThat(ticketInBDD.getOutTime()).isAfter(ticketInBDD.getInTime());
		// TODO: check that the fare generated and out time are populated correctly in
		// the database
	}

}
