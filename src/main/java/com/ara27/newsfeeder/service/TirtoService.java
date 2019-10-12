package com.ara27.newsfeeder.service;

import com.ara27.newsfeeder.domain.Articles;

import java.io.IOException;
import java.util.List;

public interface TirtoService {
    List<Articles> popularTirtoArticles() throws IOException;

    List<Articles> indepthTirtoArticles() throws IOException;

    List<Articles> allTirtoSelectedArticles() throws IOException;
}
