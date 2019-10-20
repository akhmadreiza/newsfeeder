package com.ara27.newsfeeder.service;

import com.ara27.newsfeeder.domain.Articles;

import java.util.List;

public interface GmailService {
    void sendNewsEmail(List<String> recipients, String emailContent);

    void sendNewsEmailMime(List<String> recipients, List<Articles> tirtos, List<Articles> detiks);
}
