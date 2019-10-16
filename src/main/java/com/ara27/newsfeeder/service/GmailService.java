package com.ara27.newsfeeder.service;

import java.util.List;

public interface GmailService {
    void sendNewsEmail(List<String> recipients, String emailContent);
}
