package com.wolfheros.wmedia;

import com.wolfheros.wmedia.database.SearchDatabase;
import com.wolfheros.wmedia.util.CustomerJSON;
import com.wolfheros.wmedia.util.Util;
import com.wolfheros.wmedia.value.Items;
import com.wolfheros.wmedia.value.StaticValues;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class ConnectionThread implements Runnable {
    private Connection connection;
    DataInputStream inputStream;
    ObjectOutputStream outputStream;
    Map<String, String> sMap;
    private Socket socket;

    private ConnectionThread() {
    }

    public ConnectionThread(Map<String, String> map, Socket s, Connection c) {
        this.socket = s;
        this.connection = c;
        this.sMap = map;
    }

    public void run() {
        try {
            Util.logOutput("收到客户端链接: " + StaticValues.getCurrentTime(System.currentTimeMillis()));
            this.inputStream = new DataInputStream(this.socket.getInputStream());
            String sw = this.inputStream.readUTF();
            this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            String jsonString = SearchDatabase.getInstance(sw, sw, this.connection).call();
            WMedia.setResult(StaticValues.getString(SearchDatabase.trueWord(sw)), jsonString);
            this.outputStream.writeObject(jsonString);
            this.outputStream.flush();
            try {
                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ioException) {
            ioException.printStackTrace();
            try {
                close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (Throwable th) {
            try {
                close();
            } catch (Exception e3) {
                e3.printStackTrace();
            }
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public String toJSON(List<Items> list) {
        return CustomerJSON.itemsListToJSOn(list);
    }

    /* access modifiers changed from: package-private */
    public void close() throws Exception {
        this.socket.close();
        this.outputStream.close();
        this.inputStream.close();
    }
}