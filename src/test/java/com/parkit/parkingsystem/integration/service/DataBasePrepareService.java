package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class DataBasePrepareService {

    DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    public void clearDataBaseEntries(){
        try (Connection connection = dataBaseTestConfig.getConnection();
            PreparedStatement updateParkingPreparedStatement = connection.prepareStatement("update parking set available = true");
            PreparedStatement truncateTicketPreparedStatement = connection.prepareStatement("truncate table ticket")) {
            //set parking entries to available
            updateParkingPreparedStatement.execute();

            //clear ticket entries;
            truncateTicketPreparedStatement.execute();

        }catch(SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateTicket(Ticket ticket) {
        try (Connection con = dataBaseTestConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET_FOR_TEST)) {
            ps.setTimestamp(1, new Timestamp(ticket.getInTime().getTime()));
            ps.setInt(2,ticket.getId());
            ps.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

}
