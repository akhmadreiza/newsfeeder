package com.ara27.newsfeeder.repository;

import com.ara27.newsfeeder.entity.EmailMonitoringLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailMonitoringRepository extends JpaRepository<EmailMonitoringLog, String> {
    List<EmailMonitoringLog> findAllBySubject(String subject);

    List<EmailMonitoringLog> findAllByStatus(String status);
}
