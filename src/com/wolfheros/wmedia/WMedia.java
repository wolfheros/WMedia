package com.wolfheros.wmedia;

import com.wolfheros.wmedia.database.DatabaseHelper;
import com.wolfheros.wmedia.database.SearchDatabase;
import com.wolfheros.wmedia.database.StoreDatabase;
import com.wolfheros.wmedia.http.HttpApiKt;
import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.ItemEpisode;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

public class WMedia {
    private static final int MYSQL_SCHEDULE_TIME = 15;
    private static final String HOME_PAGE = "https://ddys.mov/page/1/";
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService executors = Executors.newScheduledThreadPool(2);
    private static final ConcurrentHashMap<String, String> search_map = new ConcurrentHashMap<>();

    private static Connection connection = DatabaseHelper.build();

    private static int REQUEST_COUNT = 0;
    private static int SELF_REQUEST_COUNT = 0;

    private static ServerSocket serverSocket;
    private static Set<Items> itemsSet;

    static {
        try {
            serverSocket = new ServerSocket(StaticValues.SOCKET_PORT);
        } catch (IOException ioException) {
            Util.logOutput("Open Socket error: " + ioException.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
//        startHttpApi();
        Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        FetchDataFromInternet();
//        scheduleWork();
        waitingThread();
    }

    private static void startHttpApi(){
        try {
            HttpApiKt.startApi();
        }catch (Exception exception){
            Util.logOutput("start http api get error" + exception.getMessage());
            HttpApiKt.restartApi();
        }
    }

    private static void waitingThread() {
        try {
            executors.scheduleWithFixedDelay(WMedia::startSelfRequest,MYSQL_SCHEDULE_TIME,MYSQL_SCHEDULE_TIME,TimeUnit.SECONDS);
        }catch (Exception e){
            Util.logOutput("DATABASE RUNTIME ERROR");
            e.printStackTrace();
        }
        while (true) {
            try {
                startSocketThread(serverSocket);
                StringBuilder append = new StringBuilder().append("DATA REQUEST TIMES: ");
                int i = REQUEST_COUNT + 1;
                REQUEST_COUNT = i;
                Util.logOutput(append.append(i).toString());
            } catch (IOException | SQLException e) {
                StringBuilder append2 = new StringBuilder().append("DATA REQUEST TIMES: ");
                int i2 = REQUEST_COUNT + 1;
                REQUEST_COUNT = i2;
                Util.logOutput(append2.append(i2).toString());
                Util.logOutput("THROW EOFException");
                return;
            }
        }
    }

    private static void startSocketThread(ServerSocket serverSocket2) throws IOException, SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseHelper.build();
        }
        pool.submit(new ConnectionThread(search_map, serverSocket2.accept(), connection));
    }

    private static void startSelfRequest() {
        SearchDatabase.getInstance(StaticValues.TEST_VERSION_CODE, StaticValues.TEST_VERSION_CODE, connection).call();
        StringBuilder append = new StringBuilder().append("DATABASE SELF CONNECTION: ");
        int i = SELF_REQUEST_COUNT + 1;
        SELF_REQUEST_COUNT = i;
        Util.logOutput(append.append(i).toString());
    }

    private static void FetchMediaList(){
        itemsSet =  MediaSelection.newInstance(HOME_PAGE).run();
    }

    private static void FetchDataFromInternet() {
        FetchMediaList();
        getResource(itemsSet);
        try {
            DatabaseHelper.initialConnection();
            writeDatabase(itemsSet);
        } catch (SQLException e) {
            Util.logOutput("DATABASE RUNTIME ERROR");
        }
    }

    private static void getResource(Set<Items> itemsSet) {
        int i = 0;
        for (Items item : itemsSet) {
            i++;
            Util.logOutput("CURRENT " + i + " ITEMS, NAME: " + item.getName());
            try {
                Future<Map<Integer, List<ItemEpisode>>> future = pool.submit(new MediaCollection(item));
                if (future.get() == null || future.get().isEmpty()) {
                    Util.logOutput("Something went wrong in currency");
                } else {
                    item.setSourceMap(future.get());
                }
            }catch (Exception e) {
                Util.logOutput("Something went wrong in currency");
            }
        }
    }

    public static void writeDatabase(Set<Items> itemsSet) throws SQLException {
        for (Items item : itemsSet) {
            StoreDatabase.getInstance(item, connection).storeData();
        }
        Util.logOutput("DATABASE OPERATION FINISHED" + StaticValues.getCurrentTime(System.currentTimeMillis()));
    }

    public static void setResult(String key, String jr) {
        if (key != null) {
            synchronized (search_map) {
                search_map.put(key, jr);
            }
        }
    }

    public static String getResult(String key) {
        String str;
        synchronized (search_map) {
            str = search_map.get(key);
        }
        return str;
    }
}