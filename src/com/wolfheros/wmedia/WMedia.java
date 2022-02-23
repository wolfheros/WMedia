package com.wolfheros.wmedia;

import com.wolfheros.wmedia.database.DatabaseConnection;
import com.wolfheros.wmedia.database.SearchDatabase;
import com.wolfheros.wmedia.database.StoreDatabase;
import com.wolfheros.wmedia.http.HttpApiKt;
import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.ItemEpisode;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import io.ktor.server.netty.EngineMain;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WMedia {
    private static final int SCHEDULE_TIME = 6;
    private static final int MYSQL_SCHEDULE_TIME = 15;
    private static final String HOME_PAGE = "https://ddrk.me/page/1/";
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final ScheduledExecutorService executors = Executors.newScheduledThreadPool(2);
    private static final ConcurrentHashMap<String, String> search_map = new ConcurrentHashMap<>();

    private static Connection connection = DatabaseConnection.build();

    private static int EXECUTE_TIME = 0;
    private static int REQUEST_COUNT = 0;
    private static int SELF_REQUEST_COUNT = 0;

    private static ServerSocket serverSocket;
    private static int tryTime = 0;
    private static List<Items> listResource;

    static {
        try {
            serverSocket = new ServerSocket(StaticValues.SOCKET_PORT);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        startHttpApi();
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        delayThread();
        waitingThread();
    }

    private static void startHttpApi(){
        try {
            HttpApiKt.startApi();
        }catch (Exception exception){
            Util.logOutput("start http api get error");
            exception.printStackTrace();
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

    private static void delayThread() {
        try {
            synchronized (search_map) {
                search_map.clear();
            }
            StringBuilder append = new StringBuilder().append("START DECOMPILE JOBS: ");
            int i = EXECUTE_TIME + 1;
            EXECUTE_TIME = i;
            Util.logOutput(append.append(i).append("\n").append(StaticValues.getCurrentTime(System.currentTimeMillis())).toString());
            executors.scheduleWithFixedDelay(WMedia::nonImpactFunction,SCHEDULE_TIME,SCHEDULE_TIME,TimeUnit.HOURS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startSocketThread(ServerSocket serverSocket2) throws IOException, SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DatabaseConnection.build();
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

    private static void getResourceList(){
        listResource =  MediaSelection.newInstance(HOME_PAGE).run();
    }

    private static void getResource(){
        try {
            getResource(listResource);
            tryTime = 0;
        } catch (Exception e) {
            e.printStackTrace();
            if (tryTime < 1){
                getResource();
                tryTime += 1;
            }
        }
    }

    private static void nonImpactFunction() {
        getResourceList();
        getResource();
        try {
            writeDatabase(listResource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void getResource(List<Items> list) throws InterruptedException, ExecutionException {
        int i = 0;
        for (Items items : list) {
            i++;
            Util.logOutput("CURRENT " + i + " ITEMS, NAME: " + items.getName());
            Future<Map<Integer, List<ItemEpisode>>> future = pool.submit(new MediaCollection(items));
            if (future.get() == null || future.get().size() <= 0) {
                list.remove(items);
            } else {
                items.setSourceMap(future.get());
            }
        }
    }

    public static void writeDatabase(List<Items> list) throws SQLException {
        for (Items items : list) {
            StoreDatabase.getInstance(items, connection).getConnection();
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