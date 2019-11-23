package com.ara27.newsfeeder.repository;

import com.ara27.newsfeeder.entity.ContentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentHistoryRepository extends JpaRepository<ContentHistory, String> {
}
