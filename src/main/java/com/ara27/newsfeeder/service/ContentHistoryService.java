package com.ara27.newsfeeder.service;

import com.ara27.newsfeeder.entity.ContentHistory;

import java.util.List;

public interface ContentHistoryService {
    List<ContentHistory> fetchLatestSuccessAndMinimumContentHistory();
}
