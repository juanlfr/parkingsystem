package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

	private static final Logger logger = LogManager.getLogger("TicketDAO");

	public DataBaseConfig dataBaseConfig = new DataBaseConfig();

	public boolean saveTicket(Ticket ticket) {

		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET)) {

			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			// ps.setInt(1,ticket.getId());
			ps.setInt(1, ticket.getParkingSpot().getId());
			ps.setString(2, ticket.getVehicleRegNumber());
			ps.setDouble(3, ticket.getPrice());
			ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTime()));
			ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTime())));
			return ps.execute();
		} catch (SQLException | ClassNotFoundException ex) {
			logger.error("Error saving ticket info", ex);
		}
		return false;
	}

	public Ticket getTicket(String vehicleRegNumber) {

		Ticket ticket = null;
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET)) {
			ticket = new Ticket();
			// ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(6)), false);
				ticket.setParkingSpot(parkingSpot);
				ticket.setId(rs.getInt(2));
				ticket.setVehicleRegNumber(vehicleRegNumber);
				ticket.setPrice(rs.getDouble(3));
				ticket.setInTime(rs.getTimestamp(4));
				ticket.setOutTime(rs.getTimestamp(5));

			}
			dataBaseConfig.closeResultSet(rs);

		} catch (SQLException | ClassNotFoundException ex) {
			logger.error("Error fetching ticket info", ex);
		}
		return ticket;
	}

	public boolean updateTicket(Ticket ticket) {

		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET)) {

			ps.setDouble(1, ticket.getPrice());
			ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTime()));
			ps.setInt(3, ticket.getId());
			ps.execute();
			return true;
		} catch (SQLException | ClassNotFoundException ex) {
			logger.error("Error updating ticket info", ex);
		}
		return false;
	}

	// Count occurrencies for a same VEHICLE_REG_NUMBER
	public int getCountUserVisits(String vehicleRegNumber) {

		int userVisits = 0;
		try (Connection con = dataBaseConfig.getConnection();
				PreparedStatement ps = con.prepareStatement(DBConstants.COUNT_OCURRENCIES)) {

			ps.setString(1, vehicleRegNumber);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				userVisits = rs.getInt(1);
			}
			dataBaseConfig.closeResultSet(rs);

		} catch (SQLException | ClassNotFoundException ex) {
			logger.error("Error fetching number of user visits", ex);
		}
		return userVisits;
	}

}
