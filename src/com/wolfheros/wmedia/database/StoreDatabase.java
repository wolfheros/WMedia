package com.wolfheros.wmedia.database;

import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreDatabase {
    private Connection connection;
    private Items mItem;

    private StoreDatabase() {
    }

    private StoreDatabase(Items items, Connection con) {
        this.connection = con;
        this.mItem = items;
    }

    public static StoreDatabase getInstance(Items items, Connection con) throws SQLException {
        return new StoreDatabase(items, con);
    }

    public void getConnection() {
        storeData();
    }

    private void storeData() {
        PreparedStatement preparedStatement = null;
        try {
            ResultSet resultSet = this.connection.getMetaData().getTables((String) null, (String) null, "media_table", (String[]) null);
            ResultSet columnExist = this.connection.getMetaData().getColumns((String) null, (String) null, "media_table", "MEDIA_URL");
            if (!resultSet.next()) {
                preparedStatement = this.connection.prepareStatement(StaticValues.CREATE_DATA_SQL);
                preparedStatement.executeUpdate();
                Util.logOutput("成功创建数据库");
            }
            if (!columnExist.next()) {
                preparedStatement = this.connection.prepareStatement(StaticValues.ALTER_TABLE_NEW_COLUMN);
                preparedStatement.executeUpdate();
                Util.logOutput("插入新的字段 MEDIA_URL");
            }
            insertDatabase();
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            }
        } catch (SQLException sqlException2) {
            sqlException2.printStackTrace();
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException sqlException3) {
                    sqlException3.printStackTrace();
                }
            }
        } catch (Throwable th) {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException sqlException4) {
                    sqlException4.printStackTrace();
                }
            }
            throw th;
        }
    }

    private void insertDatabase() throws SQLException {
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
            preparedStatement.setObject(i4 + 1, this.mItem.getSourceMap());
            preparedStatement.executeUpdate();
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }
}