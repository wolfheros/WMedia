package com.wolfheros.wmedia.database;

import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.ItemEpisode;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import org.json.simple.JSONArray;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.wolfheros.wmedia.util.Util.trueWord;

public class SearchDatabase {
    private Connection connection = null;
    private int mTime = 0;
    public PreparedStatement preparedStatement;
    private ResultSet resultSet = null;
    private String searchWord = null;
    private String sqlKeyWord = null;

    private SearchDatabase(String kindWord, String sWord, Connection con) {
        this.connection = con;
        this.sqlKeyWord = StaticValues.getString(trueWord(kindWord));
        this.searchWord = trueWord(sWord);
        preRun();
    }

    public static SearchDatabase getInstance(String keyWord, String searchKeyword, Connection con) {
        return new SearchDatabase(keyWord, searchKeyword, con);
    }

    public String call() {
        if (sqlKeyWord == null){
            return null;
        }
        return Util.getJsonResult(sqlKeyWord, getKindItems());
//        Util.logOutput("获取到关键字搜索词：" + this.searchWord);
//        return CustomerJSON.itemsListToJSOn(getSearchItems("SELECT MEDIA_NAME,MEDIA_URL,IMAGE_URL,MAP_OBJECT FROM media_table WHERE MEDIA_NAME LIKE '%" + this.searchWord + "%'"));
    }

    private List<Items> getSearchItems(String sql) {
        return get(sql);
    }

    private List<Items> getKindItems() {
        return get(Util.getSQLString(sqlKeyWord));
    }

    private List<Items> get(String sql) {
        List<Items> list = new LinkedList<>();
        try {
            this.connection.isClosed();
            this.preparedStatement = this.connection.prepareStatement(sql);
            this.resultSet = this.preparedStatement.executeQuery();
            while (this.resultSet.next()) {
                Items items = new Items();
                items.setName(this.resultSet.getString(1));
                items.setMedia_url(this.resultSet.getString(2));
                items.setImage_url(this.resultSet.getString(3));
                byte[] buf = this.resultSet.getBytes("MAP_OBJECT");
                if (buf != null) {
                    items.setSourceMap((Map) new ObjectInputStream(new ByteArrayInputStream(buf)).readObject());
                }
                list.add(items);
            }
            try {
                if (this.resultSet != null) {
                    this.resultSet.close();
                }
                if (this.preparedStatement != null) {
                    this.preparedStatement.close();
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        } catch (Exception sqlException2) {
            sqlException2.printStackTrace();
            try {
                if (this.resultSet != null) {
                    this.resultSet.close();
                }
                if (this.preparedStatement != null) {
                    this.preparedStatement.close();
                }
            } catch (SQLException sqlException3) {
                sqlException3.printStackTrace();
            }
        } catch (Throwable th) {
            try {
                if (this.resultSet != null) {
                    this.resultSet.close();
                }
                if (this.preparedStatement != null) {
                    this.preparedStatement.close();
                }
            } catch (SQLException sqlException4) {
                sqlException4.printStackTrace();
            }
            throw th;
        }
        Util.logOutput("CURRENT DATABASE SIZE: " + list.size());
        return list;
    }

    /* access modifiers changed from: package-private */
    public void toJson(Items items) {
        Map<Integer, List<ItemEpisode>> map = items.getSourceMap();
        int i = 0;
        while (i < map.size()) {
            int i2 = i + 1;
            Util.logOutput(JSONArray.toJSONString(map.get(i2)));
            i = i2 + 1;
        }
    }

    private void preRun() {
        try {
            if (!this.connection.getMetaData().getTables(null, null, "media_table", null).next()) {
                this.connection.prepareStatement(StaticValues.CREATE_DATA_SQL).executeUpdate();
                Util.logOutput("SHEET CREATE FAILED !");
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}