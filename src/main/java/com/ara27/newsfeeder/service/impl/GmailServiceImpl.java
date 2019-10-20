package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.service.GmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GmailServiceImpl implements GmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    private String currDate = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm").format(LocalDateTime.now());

    @Override
    public void sendNewsEmail(List<String> recipients, String emailContent) {
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
        recipients.forEach(recipient -> {
            MimeMessagePreparator messagePreparator = mimeMessage -> {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                messageHelper.setTo(recipient);
                messageHelper.setSubject("From FeedMe! to you | Featured news and articles at " + currDate);
                Context context = new Context();
                context.setVariable("message", currDate);
                context.setVariable("tirtos", tirtos);
                context.setVariable("detiks", filterNewsSource(detiks, "detikNews", "detikHot", "detikFinance", "detikInet", "detikSport-sepakbola"));
                String content = templateEngine.process("mailTemplate", context);
                messageHelper.setText(content, true);
            };
            javaMailSender.send(messagePreparator);
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
}
