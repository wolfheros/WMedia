package com.wolfheros.wmedia.value;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StaticValues {

    public static final String ANIMATION = "动画";
    public static final String BANDAO_TV = "扳道系";
    public static final String CHINA_M = "华语电影";
    public static final String CHINA_TV = "华语剧";
    public static final String HOT_LINE = "热映中";
    public static final String JAPAN_M = "日韩电影";
    public static final String KOREA_TV = "韩剧";
    public static final String JAPAN_TV = "日剧";
    public static final String MEDIA = "综艺";
    public static final String OTHER_TV = "其他地区";
    public static final String US_M = "欧美电影";
    public static final String US_TV = "欧美剧";

    public static final String ARTICLE = "article";
    public static final String BBR = "subsrc";
    public static final String ENTRY = "entry";
    public static final String KEY = "?ddrkey=";
    public static final String KEY_VALUE = "src2";
    public static final String POST_CONTENT = "post-content";
    public static final String RESOURCE_NAME = "caption";
    public static final String RESOURCE_URL = "src0";
    public static final String PRE_URL = "https://service-k3jx48ay-1251906477.ap-hongkong.apigateway.myqcloud.com/release/hw";
    public static final int NOT_PS_CODE = 0;
    public static final int PS_CODE = 1;
    public static final String TEST_VERSION_CODE = "_V3.1";
    public static final String VERSION_CODE = "V3.1";

    public static final String ALTER_TABLE_NEW_COLUMN = "ALTER TABLE media_table ADD MEDIA_URL varchar(400) NOT NULL DEFAULT 'n' AFTER MEDIA_NAME";
    public static final String CREATE_DATA_SQL = "CREATE TABLE media_resource.media_table (MEDIA_NAME varchar(400) NOT NULL, MEDIA_URL varchar(400) NOT NULL, IMAGE_URL varchar(400) NOT NULL, LABEL_VALUE varchar(400) NOT NULL, MAP_OBJECT blob NOT NULL, PRIMARY KEY (MEDIA_NAME)) ENGINE=InnoDB DEFAULT CHARSET=utf8";
    public static final String IF_COLUMN_EXIST = "SHOW COLUMNS FROM media_table LIKE 'MEDIA_URL'";
    public static final String INSERT_DATA_SQL = "REPLACE INTO media_table(MEDIA_NAME, MEDIA_URL, IMAGE_URL, LABEL_VALUE, MAP_OBJECT) VALUES (?, ?, ?, ?, ?)";
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/media_resource?autoReconnect=true&useUnicode=yes&characterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    public static final int SOCKET_PORT = 51509;
    public static final String USER = "root";
    public static final String PASSWORD = "199219333";
    public static final String SELECT_ANIMATION = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%ANIMATION%'";
    public static final String SELECT_BANDAO_TV = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%BANDAO_TV%'";
    public static final String SELECT_CHINA_M = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%CHINA_M%'";
    public static final String SELECT_CHINA_TV = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%CHINA_TV%'";
    public static final String SELECT_HOT_LINE = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%HOT_LINE%'";
    public static final String SELECT_JAPAN_M = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%JAPAN_M%'";
    public static final String SELECT_JAPAN_TV = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%JAPAN_TV%'";
    public static final String SELECT_KOREA_TV = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%KOREA_TV%'";
    public static final String SELECT_MEDIA = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%MEDIA%'";
    public static final String SELECT_OTHER_TV = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%OTHER_TV%'";
    public static final String SELECT_US_M = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%US_M%'";
    public static final String SELECT_US_TV = "SELECT MEDIA_NAME, MEDIA_URL, IMAGE_URL, MAP_OBJECT FROM media_table WHERE LABEL_VALUE LIKE '%US_TV%'";

    public static String[] getArray(Items items) {
        List<String> list = items.getStringList();
        String[] strings = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            strings[i] = getString(list.get(i));
        }
        return strings;
    }

    public static String getString(Items items) {
        List<String> list = items.getStringList();
        StringBuilder s = new StringBuilder(" ");
        for (String s1 : list) {
            s.append(" ").append(getString(s1));
        }
        return s.toString();
    }

    public static String getString(String s) {
        char c = 99;
        switch (s) {
            case ANIMATION:
                c = 8;
                break;
            case JAPAN_TV:
                c = 5;
                break;
            case MEDIA:
                c = 4;
                break;
            case KOREA_TV:
                c = 3;
                break;
            case CHINA_TV:
                c = 10;
                break;
            case BANDAO_TV:
                break;
            case US_TV:
                c = 1;
                break;
            case HOT_LINE:
                c = 0;
                break;
            case OTHER_TV:
                c = 11;
                break;
            case CHINA_M:
                c = 9;
                break;
            case JAPAN_M:
                c = 6;
                break;
            case US_M:
                c = 2;
                break;
        }
        switch (c) {
            case 0:
                return "HOT_LINE";
            case 1:
                return "US_TV";
            case 2:
                return "US_M";
            case 3:
                return "KOREA_TV";
            case 4:
                return "MEDIA";
            case 5:
                return "JAPAN_TV";
            case 6:
                return "JAPAN_M";
            case 7:
                return "BANDAO_TV";
            case 8:
                return "ANIMATION";
            case 9:
                return "CHINA_M";
            case 10:
                return "CHINA_TV";
            case 11:
                return "OTHER_TV";
            default:
                return null;
        }
    }

    private static String getString(String[] strings) {
        return Arrays.toString(strings).replace("\\[", "(").replace("\\]", ")");
    }

    public static String getCurrentTime(long i) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").format(new Date(i));
    }
}