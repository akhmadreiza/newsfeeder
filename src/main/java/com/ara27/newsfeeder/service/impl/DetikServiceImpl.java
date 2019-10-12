package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.service.DetikService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DetikServiceImpl implements DetikService {
    @Override
    public List<Articles> listOfDetikPopularNews() throws IOException {
        List<Articles> detikArticles = new ArrayList<>();
        Document mostPopularPage = getConn("https://www.detik.com/mostpopular").get();

        Elements lastUpdateEl = mostPopularPage.getElementsByClass("updates");
        String lastUpdate = lastUpdateEl.text();

        Element popularContentDiv = mostPopularPage.getElementById("detikcontent");
        Elements popularContents = popularContentDiv.getElementsByClass("outer_box");

        Elements detikNews = popularContents.get(0).getElementsByClass("list_box").select("li");
        detikNews.forEach(content -> addArticles(detikArticles, content, "detikNews"));

        Elements detikHot = popularContents.get(1).getElementsByClass("list_box").select("li");
        detikHot.forEach(content -> addArticles(detikArticles, content, "detikHot"));

        Elements detikFinance = popularContents.get(2).getElementsByClass("list_box").select("li");
        detikFinance.forEach(content -> addArticles(detikArticles, content, "detikFinance"));

        Elements detikSport = popularContents.get(3).getElementsByClass("list_box").select("li");
        detikSport.forEach(content -> addArticles(detikArticles, content, "detikSport"));

        Elements sepakbola = popularContents.get(4).getElementsByClass("list_box").select("li");
        sepakbola.forEach(content -> addArticles(detikArticles, content, "detikSport-sepakbola"));

        Elements detikInet = popularContents.get(5).getElementsByClass("list_box").select("li");
        detikInet.forEach(content -> addArticles(detikArticles, content, "detikInet"));

        Elements detikOto = popularContents.get(6).getElementsByClass("list_box").select("li");
        detikOto.forEach(content -> addArticles(detikArticles, content, "detikOto"));

        Elements detikHealth = popularContents.get(7).getElementsByClass("list_box").select("li");
        detikHealth.forEach(content -> addArticles(detikArticles, content, "detikHealth"));

        Elements detikTravel = popularContents.get(8).getElementsByClass("list_box").select("li");
        detikTravel.forEach(content -> addArticles(detikArticles, content, "detikTravel"));

        Elements detikFood = popularContents.get(9).getElementsByClass("list_box").select("li");
        detikFood.forEach(content -> addArticles(detikArticles, content, "detikFood"));

        Elements wolipop = popularContents.get(10).getElementsByClass("list_box").select("li");
        wolipop.forEach(content -> addArticles(detikArticles, content, "wolipop"));

        Elements detikTv = popularContents.get(11).getElementsByClass("list_box").select("li");
        detikTv.forEach(content -> addArticles(detikArticles, content, "detikTv"));
        return detikArticles;
    }

    private void addArticles(List<Articles> detikArticles, Element element, String source) {
        String url = element.select("a").get(0).attributes().get("href");
        Element content = element.getElementsByClass("title_area").get(0);
        String articleTitle = content.select("h5").text();
        String articleTimeStamp = content.getElementsByClass("sponsoredby").text();

        Articles articles = new Articles();
        articles.setSource(source);
        articles.setTimestamp(articleTimeStamp);
        articles.setUrl(url);
        articles.setTitle(articleTitle);
        detikArticles.add(articles);
    }

    private Connection getConn(String url) {
        Connection connection = Jsoup.connect(url);
        connection.userAgent("Mozilla");
        return connection;
    }
}
