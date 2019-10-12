package com.ara27.newsfeeder.service;

import com.ara27.newsfeeder.domain.Articles;

import java.io.IOException;
import java.util.List;

public interface DetikService {
    List<Articles> listOfDetikPopularNews() throws IOException;
}
