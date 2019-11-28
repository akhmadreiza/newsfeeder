package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.entity.ContentHistory;
import com.ara27.newsfeeder.entity.CronjobMonitoringLog;
import com.ara27.newsfeeder.repository.ContentHistoryRepository;
import com.ara27.newsfeeder.repository.CronjobMonitoringRepository;
import com.ara27.newsfeeder.service.ContentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContentHistoryServiceImpl implements ContentHistoryService {

    @Autowired
    CronjobMonitoringRepository cronjobMonitoringRepository;

    @Autowired
    ContentHistoryRepository contentHistoryRepository;

    @Override
    public List<ContentHistory> fetchLatestSuccessAndMinimumContentHistory() {
        List<CronjobMonitoringLog> successCronJobs = cronjobMonitoringRepository.findByStatusOrderByCreatedDateDesc("SUCCESS");
        if (successCronJobs != null && !successCronJobs.isEmpty()) {
            CronjobMonitoringLog cronjobMonitoringLog = successCronJobs.get(0);
            return contentHistoryRepository.findAllByCronjobId(cronjobMonitoringLog.getId());
        }
        return null;
    }
}
