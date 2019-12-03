package com.ara27.newsfeeder.controller;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.domain.Data;
import com.ara27.newsfeeder.entity.ContentHistory;
import com.ara27.newsfeeder.entity.CronjobMonitoringLog;
import com.ara27.newsfeeder.entity.UserEntity;
import com.ara27.newsfeeder.repository.ContentHistoryRepository;
import com.ara27.newsfeeder.repository.CronjobMonitoringRepository;
import com.ara27.newsfeeder.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/ngumpuli/v1")
@EnableAsync
@CrossOrigin
public class NewsFeederController {

    public static final Logger LOGGER = LoggerFactory.getLogger(NewsFeederController.class);
    public static final String EMAIL_DEBUG_MODE = "reizaarmando@gmail.com";

    @Autowired
    DetikService detikService;

    @Autowired
    TirtoService tirtoService;

    @Autowired
    GmailService gmailService;

    @Autowired
    UserService userService;

    @Autowired
    CronjobMonitoringRepository cronjobMonitoringRepository;

    @Autowired
    ContentHistoryRepository contentHistoryRepository;

    @Autowired
    ContentHistoryService contentHistoryService;

    @GetMapping("/all")
    public ResponseEntity getAll(@RequestParam(required = false) boolean sendEmail,
                                 @RequestParam(required = false, defaultValue = "true") boolean debugMode) throws IOException {
        List<Articles> finalList = new ArrayList<>();
        List<Articles> tirto = tirtoArticles();
        List<Articles> detik = detikArticles();

        finalList.addAll(tirto);
        finalList.addAll(detik);

        if (sendEmail) {
            List<String> emailRecipient = debugMode ? constructEmailRecipients(EMAIL_DEBUG_MODE) : constructEmailRecipients();
            sendEmail(tirto, detik, emailRecipient);
        }

        return new ResponseEntity<>(new Data(finalList, finalList.size()), HttpStatus.OK);
    }

    @GetMapping("/latest-issue")
    public ResponseEntity getLatestIssue() {
        List<ContentHistory> contentHistories = contentHistoryService.fetchLatestSuccessAndMinimumContentHistory();
        List<Articles> finalList = getLatestArticlesFromContentHistories(contentHistories);
        return new ResponseEntity<>(new Data(finalList, finalList.size()), HttpStatus.OK);
    }

    private List<Articles> getLatestArticlesFromContentHistories(List<ContentHistory> contentHistories) {
        List<Articles> latestArticles = new ArrayList<>();
        for (ContentHistory contentHistory : contentHistories) {
            Articles articles = new Articles();
            articles.setSubtitle(contentHistory.getSubTitle());
            articles.setSource(contentHistory.getSource());
            articles.setTitle(contentHistory.getTitle());
            articles.setUrl(contentHistory.getUrl());
            articles.setHeader(contentHistory.getHeader());
            articles.setTimestamp(contentHistory.getTimeStamp());
            articles.setImgUrl(contentHistory.getImgUrl());
            articles.setBaseSource(contentHistory.getBaseSource());
            latestArticles.add(articles);
        }
        return latestArticles;
    }

    @GetMapping("/specific-user")
    public ResponseEntity getAllForSpecificUser(
            @RequestParam String emailAddress,
            @RequestParam(required = false) String name) throws IOException {
        userService.subscribeUserOneTimeService(emailAddress, name);
        LOGGER.info("constructing data and send email async start");
        gmailService.sendEmailAsync(emailAddress);
        LOGGER.info("constructing data and send email async end");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Scheduled(cron = "${ngumpuli.cron}")
    @GetMapping("/manual-cron")
    private void sendNewsToEmail() {
        String cronjobId = UUID.randomUUID().toString();
        LOGGER.info("feedme job invoked!");
        Long startMillis = System.currentTimeMillis();
        try {
            List<Articles> tirto = tirtoArticles();
            List<Articles> detik = detikArticles();
            List<Articles> allArticles = new ArrayList<>();
            allArticles.addAll(tirto);
            allArticles.addAll(detik);
            saveContentHistory(cronjobId, allArticles);
            List<String> emailRecipient = constructEmailRecipients();
            sendEmail(tirto, detik, emailRecipient);
            Long endMillis = System.currentTimeMillis();
            Long processingTime = endMillis - startMillis;
            cronjobMonitoringRepository.save(constructCronjobMonitoring(processingTime, "SUCCESS", null, cronjobId));
        } catch (IOException e) {
            Long endMillis = System.currentTimeMillis();
            Long processingTime = endMillis - startMillis;
            LOGGER.error("feedme job error! {}", e);
            CronjobMonitoringLog cronjobMonitoringLog = constructCronjobMonitoring(processingTime, "ERROR", e.getMessage(), cronjobId);
            cronjobMonitoringRepository.save(cronjobMonitoringLog);
            gmailService.sendEmailAlert(cronjobMonitoringLog);
        }
    }

    private void saveContentHistory(String cronjobId, List<Articles> articles) {
        Long startMillis = System.currentTimeMillis();
        List<ContentHistory> contentHistories = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Articles article : articles) {
            ContentHistory contentHistory = new ContentHistory();
            contentHistory.setCronjobId(cronjobId);
            contentHistory.setHeader(article.getHeader());
            contentHistory.setDtCreated(now);
            contentHistory.setDtCronRunning(now);
            contentHistory.setId(UUID.randomUUID().toString());
            contentHistory.setSource(article.getSource());
            contentHistory.setTitle(article.getTitle());
            contentHistory.setSubTitle(article.getSubtitle());
            contentHistory.setTimeStamp(article.getTimestamp());
            contentHistory.setUrl(article.getUrl());
            contentHistory.setImgUrl(article.getImgUrl());
            contentHistory.setBaseSource(article.getBaseSource());
            contentHistories.add(contentHistory);
        }
        contentHistoryRepository.saveAll(contentHistories);
        Long endMillis = System.currentTimeMillis();
        LOGGER.info("[saveContentHistory] took about: " + (endMillis - startMillis) + "ms");
    }

    private CronjobMonitoringLog constructCronjobMonitoring(Long processingTime, String status, String errorMessage, String cronjobId) {
        CronjobMonitoringLog cronjobMonitoringLog = new CronjobMonitoringLog();
        cronjobMonitoringLog.setId(cronjobId);
        cronjobMonitoringLog.setCreatedBy("SYSTEM");
        cronjobMonitoringLog.setCreatedDate(LocalDateTime.now());
        cronjobMonitoringLog.setStatus(status);
        cronjobMonitoringLog.setErrorMessage(errorMessage);
        cronjobMonitoringLog.setProcessingTimeMillis(processingTime);
        return cronjobMonitoringLog;
    }

    private void sendEmail(List<Articles> tirto, List<Articles> detik, List<String> recipients) {
        LOGGER.info("Start sending email to list of recipients");
        for (String recipient : recipients) {
            LOGGER.info(recipient);
        }

        gmailService.sendNewsEmailMime(recipients, tirto, detik);
    }

    private List<String> constructEmailRecipients(String... recipient) {
        List<String> recipients = new ArrayList<>();
        for (String s : recipient) {
            recipients.add(s);
        }
        return recipients;
    }

    private List<String> constructEmailRecipients() {
        List<UserEntity> allUsers = userService.getAllSubscriber();
        List<String> emails = new ArrayList<>();
        allUsers.forEach(userEntity -> {
            emails.add(userEntity.getEmailAddress());
        });
        return emails;
    }

    private List<Articles> tirtoArticles() throws IOException {
        return tirtoService.popularTirtoArticles();
    }

    private List<Articles> detikArticles() throws IOException {
        return detikService.listOfDetikPopularNews();
    }
}
