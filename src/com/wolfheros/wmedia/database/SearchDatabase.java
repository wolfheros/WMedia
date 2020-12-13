package com.wolfheros.wmedia.database;

import com.wolfheros.wmedia.WMedia;
import com.wolfheros.wmedia.util.CustomerJSON;
import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.ItemEpisode;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;

public class SearchDatabase {
    private Connection connection = null;
    private int mTime = 0;
    public PreparedStatement preparedStatement;
    private ResultSet resultSet = null;
    private String searchWord = null;
    private String sqlKeyWord = null;

    private SearchDatabase() {
    }

    private SearchDatabase(String kindWord, String sWord, Connection con) {
        this.connection = con;
        this.sqlKeyWord = StaticValues.getString(trueWord(kindWord));
        this.searchWord = trueWord(sWord);
        preRun();
    }

    public static String trueWord(String s) {
        String[] strings = s.split("_");
        if (strings[1].equals(StaticValues.VERSION_CODE)) {
            return strings[0];
        }
        return strings[1];
    }

    public static SearchDatabase getInstance(String keyWord, String searchKeyword, Connection con) {
        return new SearchDatabase(keyWord, searchKeyword, con);
    }

    public String call() {
        if (this.sqlKeyWord != null) {
            Util.logOutput("获取到SQL搜索词：" + this.sqlKeyWord);
            if (WMedia.getResult(this.sqlKeyWord) == null) {
                return CustomerJSON.itemsListToJSOn(getKindItems());
            }
            Util.logOutput("获取到搜索缓存数据");
            return WMedia.getResult(this.sqlKeyWord);
        }
        Util.logOutput("获取到关键字搜索词：" + this.searchWord);
        return CustomerJSON.itemsListToJSOn(getSearchItems("SELECT MEDIA_NAME,MEDIA_URL,IMAGE_URL,MAP_OBJECT FROM media_table WHERE MEDIA_NAME LIKE '%" + this.searchWord + "%'"));
    }

    private List<Items> getSearchItems(String sql) {
        return get(sql);
    }

    private List<Items> getKindItems() {
        return get(getSQLString());
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
        Util.logOutput("当前数据库大小为： " + list.size());
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

    private String getSQLString() {
        String sqlString;
        String str = this.sqlKeyWord;
        char c = 999;
        switch (str) {
            case "CHINA_TV" :
                c = 10;
                break;
            case "JAPAN_M":
                c = 6;
                break;
            case "ANIMATION" :
                c = 8;
                break;
            case "US_M":
                c = 2;
                break;
            case "JAPAN_TV":
                c = 5;
                break;
            case "MEDIA":
                c = 4;
                break;
            case "US_TV":
                c = 1;
                break;
            case "HOT_LINE":
                c = 0;
                break;
            case "BANDAO_TV":
                c = 7;
                break;
            case "KOREA_TV":
                c = 3;
                break;
            case "CHINA_M":
                c = 9;
                break;
        }
        switch (c) {
            case 0:
                sqlString = StaticValues.SELECT_HOT_LINE;
                break;
            case 1:
                sqlString = StaticValues.SELECT_US_TV;
                break;
            case 2:
                sqlString = StaticValues.SELECT_US_M;
                break;
            case 3:
                sqlString = StaticValues.SELECT_KOREA_TV;
                break;
            case 4:
                sqlString = StaticValues.SELECT_MEDIA;
                break;
            case 5:
                sqlString = StaticValues.SELECT_JAPAN_TV;
                break;
            case 6:
                sqlString = StaticValues.SELECT_JAPAN_M;
                break;
            case 7:
                sqlString = StaticValues.SELECT_BANDAO_TV;
                break;
            case 8:
                sqlString = StaticValues.SELECT_ANIMATION;
                break;
            case 9:
                sqlString = StaticValues.SELECT_CHINA_M;
                break;
            case 10:
                sqlString = StaticValues.SELECT_CHINA_TV;
                break;
            default:
                sqlString = StaticValues.SELECT_OTHER_TV;
                break;
        }
        Util.logOutput("生成SQL\n");
        return sqlString;
    }

    private void preRun() {
        try {
            if (!this.connection.getMetaData().getTables(null, null, "media_table", null).next()) {
                this.connection.prepareStatement(StaticValues.CREATE_DATA_SQL).executeUpdate();
                Util.logOutput("数据表不存在，创建数据表成功！");
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}