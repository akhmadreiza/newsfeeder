package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.service.TirtoService;
import com.ara27.newsfeeder.util.NewsFeederUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TirtoServiceImpl implements TirtoService {
    @Override
    public List<Articles> popularTirtoArticles() throws IOException {
        List<Articles> popularTirto = new ArrayList<>();
        Document tirtoHomePage = NewsFeederUtil.getConn("https://tirto.id").get();

        Elements subTopics = tirtoHomePage.getElementsByClass("welcome-title");
        Element containerSubTopicPopular = subTopicElSelector(subTopics, "populer");
        if (containerSubTopicPopular != null) {
            Elements subTopicPopular = containerSubTopicPopular.parent().parent().parent().getElementsByClass("col-md-6 mb-3");
            subTopicPopular.forEach(content -> addArticles(popularTirto, content, "tirto.id-populer"));
        }

        return popularTirto;
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
