package com.wolfheros.wmedia.database;

import com.google.gson.Gson;
import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StoreDatabase {
    private Connection connection;
    private Items mItem;

    private StoreDatabase(Items items, Connection con) {
        this.connection = con;
        this.mItem = items;
    }

    public static StoreDatabase getInstance(Items items, Connection con) throws SQLException {
        return new StoreDatabase(items, con);
    }

    public void storeData() throws SQLException {
        insertDatabase();
    }

    private void insertDatabase() throws SQLException {
        Gson gson = new Gson();
        Util.logOutput("数据库已经存在");
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = this.connection.prepareStatement(StaticValues.INSERT_DATA_SQL);
            int i = 0 + 1;
            preparedStatement.setString(i, this.mItem.getName());
            int i2 = i + 1;
            preparedStatement.setString(i2, this.mItem.getMedia_url());
            int i3 = i2 + 1;
            preparedStatement.setString(i3, this.mItem.getImage_url());
            int i4 = i3 + 1;
            preparedStatement.setString(i4, StaticValues.getString(this.mItem));
            preparedStatement.setString(i4 + 1, gson.toJson(this.mItem.getSourceMap()));
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}