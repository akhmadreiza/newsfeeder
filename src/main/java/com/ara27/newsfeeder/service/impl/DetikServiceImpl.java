package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.service.DetikService;
import com.ara27.newsfeeder.util.NewsFeederUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DetikServiceImpl implements DetikService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DetikServiceImpl.class);
    public static final String DETIK_MOST_POPULAR_URL = "https://www.detik.com/mostpopular";

    @Override
    public List<Articles> listOfDetikPopularNews() throws IOException {
        List<Articles> detikArticles = new ArrayList<>();
        Document mostPopularPage = null;

        Long startMillis = System.currentTimeMillis();
        for (int i = 1; i < 4; i++) {
            try {
                LOGGER.info("Attempt " + i + " to get " + DETIK_MOST_POPULAR_URL);
                mostPopularPage = NewsFeederUtil.getConn(DETIK_MOST_POPULAR_URL).get();
                LOGGER.info("Success get " + DETIK_MOST_POPULAR_URL + " on attempt number " + i + ".");
                break;
            } catch (SocketTimeoutException e) {
                LOGGER.error("SocketTimeOut on attempt number " + i + ". " + (i < 4 ? "Retrying..." : "Exiting..."));
                if (i == 3) {
                    Long endErrorMillis = System.currentTimeMillis();
                    LOGGER.info("time taken after exception occured: " + (endErrorMillis - startMillis) + "ms");
                    throw new IOException("Too long waiting for " + DETIK_MOST_POPULAR_URL + " to response.");
                }
            }
        }
        Long endSuccessMillis = System.currentTimeMillis();
        LOGGER.info("time taken to get " + DETIK_MOST_POPULAR_URL + " : " + (endSuccessMillis - startMillis) + "ms");

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

        Elements sepakbola = popularContents.get(4).getElementsByClass("list_box").select("li");
        sepakbola.forEach(content -> addArticles(detikArticles, content, "detikSport-sepakbola"));

        Elements detikInet = popularContents.get(5).getElementsByClass("list_box").select("li");
        detikInet.forEach(content -> addArticles(detikArticles, content, "detikInet"));
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
        try {
            constructSubtitle(articles);
            detikArticles.add(articles);
        } catch (Exception e) {
            LOGGER.error("failed to construct subtitle", e);
        }
    }

    private void constructSubtitle(Articles articles) throws IOException {
        Document detikDetail = null;

        Long startMillis = System.currentTimeMillis();
        for (int i = 1; i < 4; i++) {
            try {
                LOGGER.info("Attempt " + i + " to get " + articles.getUrl());
                detikDetail = NewsFeederUtil.getConn(articles.getUrl()).get();
                LOGGER.info("Success get " + articles.getUrl() + " on attempt number " + i + ".");
                break;
            } catch (SocketTimeoutException e) {
                LOGGER.error("SocketTimeOut on attempt number " + i + ". " + (i < 4 ? "Retrying..." : "Exiting..."));
                if (i == 3) {
                    Long endErrorMillis = System.currentTimeMillis();
                    LOGGER.info("time taken after exception occured: " + (endErrorMillis - startMillis) + "ms");
                    throw new IOException("Too long waiting for " + articles.getUrl() + " to response.");
                }
            }
        }
        Long endSuccessMillis = System.currentTimeMillis();
        LOGGER.info("time taken to get " + articles.getUrl() + " : " + (endSuccessMillis - startMillis) + "ms");

        Elements lastUpdateEl = detikDetail.getElementsByClass("itp_bodycontent detail_text");
        String fullContent = lastUpdateEl.get(0).text();
        String[] contentSplitByFullStop = fullContent.split("\\.");
        String firstSentences = contentSplitByFullStop[0];
        String secondSentences = contentSplitByFullStop[1];
        String first2Sentences = firstSentences + ". " + secondSentences + ".";
        articles.setSubtitle(first2Sentences);
    }
}
