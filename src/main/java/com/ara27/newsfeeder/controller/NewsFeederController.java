package com.ara27.newsfeeder.controller;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.domain.Data;
import com.ara27.newsfeeder.service.DetikService;
import com.ara27.newsfeeder.service.GmailService;
import com.ara27.newsfeeder.service.TirtoService;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class NewsFeederController {

    public static final Logger LOGGER = LoggerFactory.getLogger(NewsFeederController.class);

    @Autowired
    DetikService detikService;

    @Autowired
    TirtoService tirtoService;

    @Autowired
    GmailService gmailService;

    @GetMapping("/feedme")
    public ResponseEntity getAll(@RequestParam(required = false) boolean sendEmail) throws IOException {
        List<Articles> finalList = new ArrayList<>();
        List<Articles> tirto = tirtoArticles();
        List<Articles> detik = detikArticles();

        finalList.addAll(tirto);
        finalList.addAll(detik);

        if (sendEmail) {
            sendEmail(tirto, detik);
        }

        return new ResponseEntity<>(new Data(finalList, finalList.size()), HttpStatus.OK);
    }

    @Scheduled(cron = "${feedme.cron}")
    private void sendNewsToEmail() throws IOException {
        LOGGER.info("feedme job invoked!");
        List<Articles> tirto = tirtoArticles();
        List<Articles> detik = detikArticles();
        sendEmail(tirto, detik);
    }

    private void sendEmail(List<Articles> tirto, List<Articles> detik) throws IOException {
        List<String> recipients = new ArrayList<>();
        recipients.add("reizaarmando@gmail.com");

        LOGGER.info("Start sending email to list of recipients");
        for (String recipient : recipients) {
            LOGGER.info(recipient + "\n");
        }

        gmailService.sendNewsEmailMime(recipients, tirto, detik);
        LOGGER.info("Email sent to recipient!");
    }

    private List<Articles> tirtoArticles() throws IOException {
        return tirtoService.popularTirtoArticles();
    }

    private List<Articles> detikArticles() throws IOException {
        return detikService.listOfDetikPopularNews();
    }

    private String emailText(List... contents) throws IOException {
        String text = "Hi there!" +
                "\n \n" +
                "Saya Akhmad Reiza Armando dan kalian pasti kenal saya wkwk. " +
                "Kalian menerima email ini karena kalian adalah keluarga saya, pacar saya, teman saya atau rekan kerja saya :D\n\n" +

                "Sebelumnya, saya mau ngucapin terima kasih buat kalian yang udah mau (dipaksa wkwk) menerima newsletter ini. " +
                "Kalo kalian males menerima email ini lagi, silakan hubungi saya via channel apapun (WA boleh). " +
                "Oiya sama satu lagi. Kalo kalian rasa keluarga, pacar, teman, bos, kalian membutuhkan email ini, silakan kirimkan alamat email nya ke email" +
                "pribadi saya di reizaarmando@gmail.com dengan subject [FeedMe! Subscription Request]. Oke?" +

                "\n\nMohon maaf dan thank you yaaa gaes sebelumnya :D\n" +
                "\n" +

                "Anyway, kalo boleh cerita dikit, jadi email ini tuh hasil generate-an dari versi early development dari program yang saya buat. " +
                "Program yang saya buat ini namanya FeedMe! Apa itu? FeedMe! adalah sebuah program web-crawler yang nge rangkum berita populer dari beberapa sumber. " +
                "Saat ini sayangnya sumber berita yang saya ambil baru dari dua sumber aja, yaitu detik.com dan tirto.id. " +
                "Kedepannya akan saya tambahin lagi source nya yaa gengs!\n\n" +
                "Beklah, langsung saja!" +
                "\n\n" +
                "Berikut adalah artikel dan berita populer saat ini: ";

        text = text + selectedArticlesAndNews(contents[0], contents[1]);

        //footer
        text = text + "\n\n\n" + "Thanks and Regards,\nAkhmad Reiza Armando from FeedMe!";

        return text;
    }

    private String selectedArticlesAndNews(List<Articles> tirto, List<Articles> detik) {
        int tirtoIndex = 1;
        int detikIndex = 1;

        String text = "\n\n" +
                "";

        text = text + "Populer di Tirto - https://tirto.id\n\n";
        for (Articles articles : tirto) {
            if (articles.getSource().equalsIgnoreCase("tirto.id-populer") ||
                    articles.getSource().equalsIgnoreCase("tirto.id-indept")) {
                text = text + tirtoIndex + ". " + articles.getTitle() + " | " + articles.getUrl() + "\n\n";
                tirtoIndex++;
            }
        }

        text = text + "\n";

        text = text + "Populer di Detik - https://detik.com\n\n";
        for (Articles articles : detik) {
            if (articles.getSource().equalsIgnoreCase("detikNews")
                    || articles.getSource().equalsIgnoreCase("detikHot")
                    || articles.getSource().equalsIgnoreCase("detikFinance")) {
                text = text + detikIndex + ". " + articles.getTitle() + " | " + articles.getUrl() + "\n\n";
                detikIndex++;
            }
        }

        text = text + "\n";

        return text;
    }
}
