package com.ara27.newsfeeder.util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class NewsFeederUtil {
    public static Connection getConn(String url) {
        Connection connection = Jsoup.connect(url);
        connection.userAgent("Mozilla");
        return connection;
    }
}
