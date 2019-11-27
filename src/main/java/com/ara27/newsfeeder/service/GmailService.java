package com.ara27.newsfeeder.service;

import com.ara27.newsfeeder.domain.Articles;

import java.io.IOException;
import java.util.List;

public interface GmailService {
    void sendNewsEmail(List<String> recipients, String emailContent);

    void sendNewsEmailMime(List<String> recipients, List<Articles> tirtos, List<Articles> detiks);

    void sendEmailAlert(Object errObj);

    void sendEmailAsync(String emailAddress) throws IOException;
}
