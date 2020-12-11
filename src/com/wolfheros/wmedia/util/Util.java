package com.wolfheros.wmedia.util;

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
}