package com.ara27.newsfeeder.repository;

import com.ara27.newsfeeder.entity.ContentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentHistoryRepository extends JpaRepository<ContentHistory, String> {
    List<ContentHistory> findAllByCronjobId(String cronjobId);
}
