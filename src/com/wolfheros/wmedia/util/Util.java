package com.wolfheros.wmedia.util;

import com.wolfheros.wmedia.WMedia;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public class Util {
    public static String cutUri(String uri) {
        return uri.split("\\(")[1].split("\\)")[0];
    }

    public static boolean isEqual(String next_page, String current_page) {
        return parseInt(next_page) == parseInt(current_page) + -1;
    }

    private static int parseInt(String s) {
        String[] nextString = s.split("/");
        return Integer.parseInt(nextString[nextString.length - 1]);
    }

    public static boolean isBigger(String next_page, String current_page) {
        return parseInt(next_page) > parseInt(current_page);
    }

    public static boolean isNumber(String s) {
        return Pattern.compile("-?\\d+(\\.\\d+)?").matcher(s).matches();
    }

    public static void logOutput(String s) {
        System.out.println(s);
    }

    public static void threadSleep() throws InterruptedException {
        long time = (long) (Math.random() * 5000.0d);
        logOutput("获取资源线程随机休眠：" + time);
        Thread.sleep(time);
    }

    public static String getSQLString(@NotNull String trueSqlWord) {
        String sqlString;
        switch (trueSqlWord) {
            case "HOT_LINE":
                sqlString = StaticValues.SELECT_HOT_LINE;
                break;
            case "US_TV":
                sqlString = StaticValues.SELECT_US_TV;
                break;
            case "US_M":
                sqlString = StaticValues.SELECT_US_M;
                break;
            case "KOREA_TV":
                sqlString = StaticValues.SELECT_KOREA_TV;
                break;
            case "MEDIA":
                sqlString = StaticValues.SELECT_MEDIA;
                break;
            case "JAPAN_TV":
                sqlString = StaticValues.SELECT_JAPAN_TV;
                break;
            case "JAPAN_M":
                sqlString = StaticValues.SELECT_JAPAN_M;
                break;
            case "BANDAO_TV":
                sqlString = StaticValues.SELECT_BANDAO_TV;
                break;
            case "ANIMATION" :
                sqlString = StaticValues.SELECT_ANIMATION;
                break;
            case "CHINA_M":
                sqlString = StaticValues.SELECT_CHINA_M;
                break;
            case "CHINA_TV" :
                sqlString = StaticValues.SELECT_CHINA_TV;
                break;
            default:
                sqlString = StaticValues.SELECT_OTHER_TV;
                break;
        }
        Util.logOutput("生成SQL\n");
        return sqlString;
    }

    public static String trueWord(@NotNull String s) {
        String[] strings = s.split("_");
        if (strings[1].equals(StaticValues.VERSION_CODE)) {
            return strings[0];
        }
        return strings[1];
    }

    public static String getJsonResult(String keyWord, List<Items> list){
        if (keyWord!= null) {
            Util.logOutput("获取到SQL搜索词：" + keyWord);
            if (WMedia.getResult(keyWord) == null) {
                return CustomerJSON.itemsListToJSOn(list);
            }
            Util.logOutput("获取到搜索缓存数据");
            return WMedia.getResult(keyWord);
        }
        return null;
//        Util.logOutput("获取到关键字搜索词：" + keyWord);
//        return CustomerJSON.itemsListToJSOn(getSearchItems("SELECT MEDIA_NAME,MEDIA_URL,IMAGE_URL,MAP_OBJECT FROM media_table WHERE MEDIA_NAME LIKE '%" + this.searchWord + "%'"));

    }
}