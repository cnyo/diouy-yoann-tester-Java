package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

}
