package com.wolfheros.wmedia;

import com.wolfheros.wmedia.database.DatabaseConnection;
import com.wolfheros.wmedia.database.SearchDatabase;
import com.wolfheros.wmedia.database.StoreDatabase;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WMedia {
    private static int EXECUTE_TIME = 0;
    private static final String HOME_PAGE = "https://ddrk.me/page/1/";
    private static int REQUEST_COUNT = 0;
    private static int SELF_REQUEST_COUNT = 0;
    private static Connection connection = DatabaseConnection.build();
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final ConcurrentHashMap<String, String> search_map = new ConcurrentHashMap<>();
    private static ServerSocket serverSocket;

    private static void delayThread() {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.wolfheros.wmedia.WMedia.delayThread():void, dex: WMediav3.3.4.jar
        */
        throw new UnsupportedOperationException("Method not decompiled: com.wolfheros.wmedia.WMedia.delayThread():void");
    }

    private static void startSelfRequest() {
        /*
        // Can't load method instructions: Load method exception: Unknown instruction: 'invoke-custom' in method: com.wolfheros.wmedia.WMedia.startSelfRequest():void, dex: WMediav3.3.4.jar
        */
        throw new UnsupportedOperationException("Method not decompiled: com.wolfheros.wmedia.WMedia.startSelfRequest():void");
    }

    static {
        try {
            serverSocket = new ServerSocket(StaticValues.SOCKET_PORT);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        delayThread();
        waitingThread();
    }

    private static void waitingThread() {
        startSelfRequest();
        while (true) {
            try {
                startSocketThread(serverSocket);
                StringBuilder append = new StringBuilder().append("网络请求次数：");
                int i = REQUEST_COUNT + 1;
                REQUEST_COUNT = i;
                Util.logOutput(append.append(i).toString());
            } catch (IOException | SQLException e) {
                StringBuilder append2 = new StringBuilder().append("网络请求次数：");
                int i2 = REQUEST_COUNT + 1;
                REQUEST_COUNT = i2;
                Util.logOutput(append2.append(i2).toString());
                Util.logOutput("抛出 EOFException");
                return;
            }
        }
    }

    private static /* synthetic */ void lambda$delayThread$0() {
        try {
            synchronized (search_map) {
                search_map.clear();
            }
            StringBuilder append = new StringBuilder().append("开始执行延迟任务: ");
            int i = EXECUTE_TIME + 1;
            EXECUTE_TIME = i;
            Util.logOutput(append.append(i).append("\n").append(StaticValues.getCurrentTime(System.currentTimeMillis())).toString());
            nonImpactFunction();
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

    private static /* synthetic */ void lambda$startSelfRequest$1() {
        SearchDatabase.getInstance(StaticValues.TEST_VERSION_CODE, StaticValues.TEST_VERSION_CODE, connection).call();
        StringBuilder append = new StringBuilder().append("数据库自连次数：");
        int i = SELF_REQUEST_COUNT + 1;
        SELF_REQUEST_COUNT = i;
        Util.logOutput(append.append(i).toString());
    }

    private static void nonImpactFunction() throws Exception {
        List<Items> list = MediaSelection.newInstance(HOME_PAGE).run();
        getResource(list);
        writeDatabase(list);
    }

    private static void getResource(List<Items> list) throws InterruptedException, ExecutionException {
        int i = 0;
        for (Items items : list) {
            i++;
            Util.logOutput("当前第" + i + "个item，项目名称：" + items.getName());
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
        Util.logOutput("数据库操作队列已结束。" + StaticValues.getCurrentTime(System.currentTimeMillis()));
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