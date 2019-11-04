package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.service.TirtoService;
import com.ara27.newsfeeder.util.NewsFeederUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TirtoServiceImpl implements TirtoService {

    @Value("${feedme.max.article.tirto}")
    String maxArticleCount;

    public static final Logger LOGGER = LoggerFactory.getLogger(TirtoServiceImpl.class);
    public static final String TIRTO_MOST_POPULAR_URL = "https://tirto.id/search";

    @Override
    public List<Articles> popularTirtoArticles() throws IOException {
        List<Articles> popularTirto = new ArrayList<>();
        Document tirtoHomePage = null;

        Long startMillis = System.currentTimeMillis();
        for (int i = 1; i < 4; i++) {
            try {
                LOGGER.info("Attempt " + i + " to get " + TIRTO_MOST_POPULAR_URL);
                tirtoHomePage = NewsFeederUtil.getConn(TIRTO_MOST_POPULAR_URL).get();
                LOGGER.info("Success get " + TIRTO_MOST_POPULAR_URL + " on attempt number " + i + ".");
                break;
            } catch (SocketTimeoutException e) {
                LOGGER.error("SocketTimeOut on attempt number " + i + ". " + (i < 4 ? "Retrying..." : "Exiting..."));
                if (i == 3) {
                    Long endErrorMillis = System.currentTimeMillis();
                    LOGGER.info("time taken after exception occured: " + (endErrorMillis - startMillis) + "ms");
                    throw new IOException("Too long waiting for " + TIRTO_MOST_POPULAR_URL + " to response.");
                }
            }
        }
        Long endSuccessMillis = System.currentTimeMillis();
        LOGGER.info("time taken to get " + TIRTO_MOST_POPULAR_URL + " : " + (endSuccessMillis - startMillis) + "ms");

        Elements elPopularArticles = tirtoHomePage.getElementsByClass("col-md-4 mb-4 news-list-fade");
        for (int i = 0; i < Integer.parseInt(maxArticleCount); i++) {
            Element elPopularArticle = elPopularArticles.get(i);
            String baseUrl = "https://tirto.id";
            String url = baseUrl + elPopularArticle.select("div a").last().attributes().get("href");
            String articleTitle = elPopularArticle.select("div a").last().select("h1").text();

            Articles articles = new Articles();
            articles.setSource("tirto.id-populer");
            articles.setUrl(url);
            articles.setTitle(articleTitle);
            constructSubtitle(articles);
            popularTirto.add(articles);
        }

        return popularTirto;
    }

    private void constructSubtitle(Articles articles) throws IOException {
        Document tirtoNewsPage = null;

        Long startMillis = System.currentTimeMillis();
        for (int i = 1; i < 4; i++) {
            try {
                LOGGER.info("Attempt " + i + " to get " + articles.getUrl());
                tirtoNewsPage = NewsFeederUtil.getConn(articles.getUrl()).get();
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

        String subtitle = tirtoNewsPage.getElementsByClass("italic ringkasan mb-2").isEmpty() ? null :
                tirtoNewsPage.getElementsByClass("italic ringkasan mb-2").get(0).text();

        articles.setSubtitle(subtitle);
    }

    @Override
    public List<Articles> indepthTirtoArticles() throws IOException {
        List<Articles> indepthTirto = new ArrayList<>();
        Document tirtoHomePage = NewsFeederUtil.getConn("https://tirto.id").get();

        Elements subTopics = tirtoHomePage.getElementsByClass("welcome-title");
        Element containerSubTopicIndepth = subTopicElSelector(subTopics, "INDEPTH");
        if (containerSubTopicIndepth != null) {
            Elements subTopicPopular = containerSubTopicIndepth.parent().parent().parent().parent().getElementsByClass("mb-3");
            subTopicPopular.forEach(content -> addArticles(indepthTirto, content, "tirto.id-indepth"));
        }

        return indepthTirto;
    }

    @Override
    public List<Articles> allTirtoSelectedArticles() throws IOException {
        List<Articles> allSelectedTirto = new ArrayList<>();
        Document tirtoHomePage = NewsFeederUtil.getConn("https://tirto.id").get();

        Elements subTopics = tirtoHomePage.getElementsByClass("welcome-title");

        Element containerSubTopicPopular = subTopicElSelector(subTopics, "populer");
        if (containerSubTopicPopular != null) {
            Elements subTopicPopular = containerSubTopicPopular.parent().parent().parent().getElementsByClass("col-md-6 mb-3");
            subTopicPopular.forEach(content -> addArticles(allSelectedTirto, content, "tirto.id-populer"));
        }

        Element containerSubTopicIndepth = subTopicElSelector(subTopics, "INDEPTH");
        if (containerSubTopicIndepth != null) {
            Elements subTopicPopular = containerSubTopicIndepth.parent().parent().parent().parent().getElementsByClass("mb-3");
            subTopicPopular.forEach(content -> addArticles(allSelectedTirto, content, "tirto.id-indepth"));
        }

        return allSelectedTirto;
    }

    private void addArticles(List<Articles> tirtoArticles, Element element, String source) {
        Element urlEl = element.select("a").get(0);
        String url = urlEl.baseUri() + urlEl.attributes().get("href");
        String articleTitle = element.select("h1").text();

        Articles articles = new Articles();
        articles.setSource(source);
        articles.setUrl(url);
        articles.setTitle(articleTitle);
        tirtoArticles.add(articles);
    }


    private Element subTopicElSelector(Elements elementsSubTopic, String querySubTopic) {
        Element containerSubTopic = null;
        for (int i = 0; i < elementsSubTopic.size(); i++) {
            Element subTopic = elementsSubTopic.get(i);
            if (subTopic.text().equalsIgnoreCase(querySubTopic)) {
                containerSubTopic = elementsSubTopic.get(i);
                break;
            }
        }
        return containerSubTopic;
    }
}
