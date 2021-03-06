package org.alexdev.kepler.dao.mysql;

import org.alexdev.kepler.dao.Storage;

import java.sql.*;
import java.util.HashMap;

public class FuserightsDao {
    
    /**
     * Get the fuserights.
     */
    public static HashMap<String, Integer> getFuserights() {
        HashMap<String, Integer> fuserights = new HashMap<>();

        Connection sqlConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            sqlConnection = Storage.getStorage().getConnection();
            preparedStatement = Storage.getStorage().prepare("SELECT * FROM rank_fuserights", sqlConnection);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                fuserights.put(resultSet.getString("fuseright"), resultSet.getInt("min_rank"));
            }

        } catch (Exception e) {
            Storage.logError(e);
        } finally {
            Storage.closeSilently(resultSet);
            Storage.closeSilently(preparedStatement);
            Storage.closeSilently(sqlConnection);
        }

        return fuserights;
    }
}
