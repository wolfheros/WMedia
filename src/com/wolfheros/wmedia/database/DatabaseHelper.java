package com.wolfheros.wmedia.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.StaticValues;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseHelper {
    public static Connection connection = null;

    public static Connection build() {
        Util.logOutput("DATABASE INITIAL");
        try {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setUrl(StaticValues.JDBC_URL);
            ds.setUser(StaticValues.USER);
            ds.setPassword(StaticValues.PASSWORD);
            connection = ds.getConnection();
        } catch (SQLException sqlException) {
            Util.logOutput("ERROR DATABASE!");
            sqlException.printStackTrace();
        }
        return connection;
    }

    public static void initialConnection() throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = connection.getMetaData().getTables(null, null, "media_table", null);
        ResultSet columExist = connection.getMetaData().getColumns(null, null, "media_table", "MEDIA_URL");
        if (!resultSet.next()) {
            preparedStatement = connection.prepareStatement(StaticValues.CREATE_DATA_SQL);
            preparedStatement.execute();
            Util.logOutput("DATABASE INITIAL CONNECTION");
        }
    }
}