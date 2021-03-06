package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.service.DetikService;
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
public class DetikServiceImpl implements DetikService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DetikServiceImpl.class);
    public static final String DETIK_MOST_POPULAR_URL = "https://www.detik.com/mostpopular";
    public static final String THREE_DOTS = "...";

    @Value("${ngumpuli.max.article.detik}")
    String maxArticleCount;

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

        Elements elements = mostPopularPage.getElementsByClass("grid-row list-content").get(0).getElementsByClass("list-content__item");
        for (Element eachElement : elements) {
            String imgUrl = eachElement.select("img").attr("src");
            String newsUrl = eachElement.getElementsByClass("media__title").select("a").attr("href");
            String newsTitle = eachElement.getElementsByClass("media__title").select("a").text();

            Articles articles = new Articles();
            articles.setBaseSource("detik.com");
            articles.setUrl(newsUrl);
            articles.setTitle(newsTitle);
            articles.setImgUrl(imgUrl);
            constructNewsDetailElement(articles);
            detikArticles.add(articles);
        }

        return detikArticles;
    }

    private void constructNewsDetailElement(Articles articles) throws IOException {
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

        articles.setTimestamp(getTimeStampElement(detikDetail));
        articles.setSubtitle(getSubtitle(detikDetail));
    }

    private String getTimeStampElement(Document detikDetail) {
        try {
            return detikDetail.getElementsByClass("detail__date").get(0).text();
        } catch (Exception e) {
            LOGGER.error("error get timestamp on class detail__date");
        }

        try {
            return detikDetail.getElementsByClass("date").get(0).text();
        } catch (Exception e) {
            LOGGER.error("error get timestamp on class date");
        }

        return null;
    }

    private String getSubtitle(Document detikDetail) {
        try {
            return detikDetail.getElementsByClass("detail__body-text").get(0).text().split("\\.")[0] + THREE_DOTS;
        } catch (Exception e) {
            LOGGER.error("error get subtitle on detail__body-text");
        }

        try {
            return detikDetail.getElementsByClass("itp_bodycontent detail_text").get(0).text().split("\\.")[0] + THREE_DOTS;
        } catch (Exception e) {
            LOGGER.error("error get subtitle on itp_bodycontent detail_text");
        }

        //wolipop
        try {
            return detikDetail.getElementsByClass("itp_bodycontent detail_text group").get(0).text().split("\\.")[0] + THREE_DOTS;
        } catch (Exception e) {
            LOGGER.error("error get subtitle on itp_bodycontent detail_text group");
        }

        //detikTravel
        try {
            return detikDetail.getElementsByClass("itp_bodycontent read__content pull-left").get(0).text().split("\\.")[0] + THREE_DOTS;
        } catch (Exception e) {
            LOGGER.error("error get subtitle on itp_bodycontent read__content pull-left");
        }

        return null;
    }

    @Deprecated
    private void addArticles(List<Articles> detikArticles, Element element, String source) {
        String url = element.select("a").get(0).attributes().get("href");
        Element content = element.getElementsByClass("title_area").get(0);
        String articleTitle = content.select("h5").text();
        String articleTimeStamp = content.getElementsByClass("sponsoredby").text();

        Articles articles = new Articles();
        articles.setBaseSource("detik.com");
        articles.setSource(source);
        articles.setTimestamp(articleTimeStamp);
        articles.setUrl(url);
        articles.setTitle(articleTitle);
        try {
            constructSubtitleAndImgUrl(articles);
        } catch (Exception e) {
            LOGGER.error("failed to construct subtitle", e);
        }
        detikArticles.add(articles);
    }

    @Deprecated
    private void constructSubtitleAndImgUrl(Articles articles) throws IOException {
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

        if (detikDetail.getElementsByClass("itp_bodycontent detail_text") != null
                && !detikDetail.getElementsByClass("itp_bodycontent detail_text").isEmpty()) {
            LOGGER.info("[constructSubtitleAndImgUrl] element itp_bodycontent detail_text found! source: {}", articles.getSource());
            lastUpdateEl = detikDetail.getElementsByClass("itp_bodycontent detail_text");
        } else if (detikDetail.getElementsByClass("detail_text") != null
                && !detikDetail.getElementsByClass("detail_text").isEmpty()) {
            LOGGER.info("[constructSubtitleAndImgUrl] element detail_text found! source: {}", articles.getSource());
            lastUpdateEl = detikDetail.getElementsByClass("detail_text");
        } else if (detikDetail.getElementsByClass("detail__body-text") != null
                && !detikDetail.getElementsByClass("detail__body-text").isEmpty()) {
            LOGGER.info("[constructSubtitleAndImgUrl] element detail__body-text found! source: {}", articles.getSource());
            lastUpdateEl = detikDetail.getElementsByClass("detail__body-text");
        } else {
            LOGGER.info("[constructSubtitleAndImgUrl][ERR-SCRAPPING] cannot found any element defined! please fix the logic! source: {}, url: {}", articles.getSource(), articles.getUrl());
        }

        try {
            String fullContent = lastUpdateEl.get(0).text();
            String[] contentSplitByFullStop = fullContent.split("\\.");
            String firstSentences = contentSplitByFullStop[0];
            String secondSentences = contentSplitByFullStop[1];
            String first2Sentences = firstSentences + ". " + secondSentences + ".";
            articles.setSubtitle(first2Sentences);
        } catch (Exception e) {
            LOGGER.error("[constructSubtitleAndImgUrl] failed to construct subtitle! ", e);
        }

        try {
            constructImgUrl(articles, detikDetail);
        } catch (Exception e) {
            LOGGER.error("[constructSubtitleAndImgUrl] failed to construct imgUrl! ", e);
        }
    }

    @Deprecated
    public void constructImgUrl(Articles articles, Document detikDetail) {
        if (!detikDetail.getElementsByClass("pic_artikel").isEmpty()
                && detikDetail.getElementsByClass("pic_artikel").get(0) != null
                && !detikDetail.getElementsByClass("pic_artikel").get(0).select("img").isEmpty()) {
            LOGGER.info("[constructImgUrl] element pic_artikel found! source: {}", articles.getSource());
            articles.setImgUrl(detikDetail.getElementsByClass("pic_artikel").get(0).select("img").attr("src"));
        } else if (!detikDetail.getElementsByClass("detail__media-image").isEmpty()
                && detikDetail.getElementsByClass("detail__media-image").get(0) != null
                && !detikDetail.getElementsByClass("detail__media-image").get(0).select("img").isEmpty()) {
            LOGGER.info("[constructImgUrl] element detail__media-image found! source: {}", articles.getSource());
            articles.setImgUrl(detikDetail.getElementsByClass("detail__media-image").get(0).select("img").attr("src"));
        } else if (!detikDetail.getElementsByClass("media_artikel wide").isEmpty()
                && detikDetail.getElementsByClass("media_artikel wide").get(0) != null
                && !detikDetail.getElementsByClass("media_artikel wide").get(0).select("img").isEmpty()) {
            LOGGER.info("[constructImgUrl] element media_artikel wide found! source: {}", articles.getSource());
            articles.setImgUrl(detikDetail.getElementsByClass("media_artikel wide").get(0).select("img").attr("src"));
        } else {
            LOGGER.info("[constructImgUrl][ERR-SCRAPPING] cannot found any element defined! please fix the logic! source: {}, url: {}", articles.getSource(), articles.getUrl());
        }
    }
}
