package com.ara27.newsfeeder.controller;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.domain.Data;
import com.ara27.newsfeeder.entity.CronjobMonitoringLog;
import com.ara27.newsfeeder.entity.UserEntity;
import com.ara27.newsfeeder.repository.CronjobMonitoringRepository;
import com.ara27.newsfeeder.service.DetikService;
import com.ara27.newsfeeder.service.GmailService;
import com.ara27.newsfeeder.service.TirtoService;
import com.ara27.newsfeeder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feedme/v1")
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

    @Scheduled(cron = "${feedme.cron}")
    private void sendNewsToEmail() {
        LOGGER.info("feedme job invoked!");
        Long startMillis = System.currentTimeMillis();
        try {
            List<Articles> tirto = tirtoArticles();
            List<Articles> detik = detikArticles();
            List<String> emailRecipient = constructEmailRecipients();
            sendEmail(tirto, detik, emailRecipient);
            Long endMillis = System.currentTimeMillis();
            Long processingTime = endMillis - startMillis;
            cronjobMonitoringRepository.save(cronjobMonitoringRepository.save(constructCronjobMonitoring(processingTime, "SUCCESS", null)));
        } catch (IOException e) {
            Long endMillis = System.currentTimeMillis();
            Long processingTime = endMillis - startMillis;
            LOGGER.error("feedme job error! {}", e);
            CronjobMonitoringLog cronjobMonitoringLog = constructCronjobMonitoring(processingTime, "ERROR", e.getMessage());
            cronjobMonitoringRepository.save(cronjobMonitoringLog);
            gmailService.sendEmailAlert(cronjobMonitoringLog);
        }
    }

    private CronjobMonitoringLog constructCronjobMonitoring(Long processingTime, String status, String errorMessage) {
        CronjobMonitoringLog cronjobMonitoringLog = new CronjobMonitoringLog();
        cronjobMonitoringLog.setId(UUID.randomUUID().toString());
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
