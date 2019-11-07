package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.entity.EmailMonitoringLog;
import com.ara27.newsfeeder.repository.EmailMonitoringRepository;
import com.ara27.newsfeeder.service.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.InternetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GmailServiceImpl implements GmailService {

    public static final Logger LOGGER = LoggerFactory.getLogger(GmailServiceImpl.class);

    @Value("${feedme.application.url}")
    String baseUrl;

    @Value("${feedme.subscribe.url}")
    String subscribeUrl;

    @Value("${feedme.unsubscribe.url}")
    String unsubscribeUrl;

    @Value("${spring.mail.username}")
    String fromAddress;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    EmailMonitoringRepository emailMonitoringRepository;

    @Override
    public void sendNewsEmail(List<String> recipients, String emailContent) {
        String currDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(LocalDateTime.now());
        recipients.forEach(recipient -> {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(recipient);
            mailMessage.setSubject("From FeedMe! to you | Featured news and articles at " + currDate);
            mailMessage.setText(emailContent);
            javaMailSender.send(mailMessage);
        });
    }

    @Override
    public void sendNewsEmailMime(List<String> recipients, List<Articles> tirtos, List<Articles> detiks) {
        LocalDateTime currLocalDateTime = LocalDateTime.now();
        String currDate = DateTimeFormatter.ofPattern("dd-MMM").format(currLocalDateTime);
        String currHHmm = DateTimeFormatter.ofPattern("HH:mm").format(currLocalDateTime);
        recipients.forEach(recipient -> {
            String subject = "Berita & Artikel Populer " + currDate + " Pukul " + currHHmm;
            String status = "INIT";
            String errMessage = null;
            LOGGER.info("[sendNewsEmailMime] recipient: " + recipient);
            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                messageHelper.setTo(recipient);
                messageHelper.setSubject(subject);
                messageHelper.setFrom(new InternetAddress(fromAddress, "Reiza dari FeedMe!"));
                Context context = new Context();
                context.setVariable("message", currDate + " Pukul " + currHHmm);
                context.setVariable("tirtos", tirtos);
                context.setVariable("detiks", detiks);
                context.setVariable("subscribeUrl", baseUrl + subscribeUrl + recipient);
                context.setVariable("unsubscribeUrl", baseUrl + unsubscribeUrl + recipient);
                context.setVariable("teaser", "Dirangkum dari Detik: " + filterNewsSource(detiks, "detikNews").get(0).getTitle()
                        + " | Dirangkum dari Tirto: " + tirtos.get(0).getTitle());
                String content = templateEngine.process("mailTemplate", context);
                messageHelper.setText(content, true);

            };
            Long startMillis = System.currentTimeMillis();
            try {
                javaMailSender.send(messagePreparator);
                status = "SUCCESS";
                LOGGER.info("Email sent to recipients!");
            } catch (Exception e) {
                status = "FAILED";
                errMessage = e.getMessage();
                LOGGER.error("[sendNewsEmailMime] error! {}", e);
                LOGGER.info("Email not sent to recipients!");
            }
            Long endMillis = System.currentTimeMillis();
            emailMonitoringRepository.save(constructMonitoringLog(subject, fromAddress, recipient, (endMillis - startMillis), status, errMessage));
        });
    }

    private List<Articles> filterNewsSource(List<Articles> detiks, String... source) {
        List<Articles> detikNews = new ArrayList<>();
        for (Articles detikNewsOnly : detiks) {
            for (String filter : source) {
                if (detikNewsOnly.getSource().equalsIgnoreCase(filter)) {
                    detikNews.add(detikNewsOnly);
                }
            }
        }
        return detikNews;
    }

    private EmailMonitoringLog constructMonitoringLog(String subject, String from, String recipient, Long processingTime, String status, String errMessage) {
        EmailMonitoringLog emailMonitoringLog = new EmailMonitoringLog();
        emailMonitoringLog.setCreatedBy("SYSTEM");
        emailMonitoringLog.setCreatedDate(LocalDateTime.now());
        emailMonitoringLog.setSubject(subject);
        emailMonitoringLog.setEmailSender(from);
        emailMonitoringLog.setEmailRecipient(recipient);
        emailMonitoringLog.setId(UUID.randomUUID().toString());
        emailMonitoringLog.setEmailProcTime(processingTime);
        emailMonitoringLog.setStatus(status);
        emailMonitoringLog.setErrMessage(errMessage);
        return emailMonitoringLog;
    }
}
