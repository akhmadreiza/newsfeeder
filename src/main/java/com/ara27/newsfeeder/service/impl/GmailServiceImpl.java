package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.service.GmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class GmailServiceImpl implements GmailService {

    @Autowired
    JavaMailSender javaMailSender;

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
}
