package com.ara27.newsfeeder.repository;

import com.ara27.newsfeeder.entity.CronjobMonitoringLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CronjobMonitoringRepository extends JpaRepository<CronjobMonitoringLog, String> {
    List<CronjobMonitoringLog> findAllByStatus(String status);
}
