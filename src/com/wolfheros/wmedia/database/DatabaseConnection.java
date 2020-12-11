package com.wolfheros.wmedia.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.StaticValues;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection connection = null;

    public static Connection build() {
        Util.logOutput("启动数据库链接方法！");
        try {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setUrl(StaticValues.JDBC_URL);
            ds.setUser(StaticValues.USER);
            ds.setPassword(StaticValues.PASSWORD);
            connection = ds.getConnection();
        } catch (SQLException sqlException) {
            Util.logOutput("操作数据库过程中出错！");
            sqlException.printStackTrace();
        }
        return connection;
    }
}