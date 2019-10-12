package com.ara27.newsfeeder.controller;

import com.ara27.newsfeeder.domain.Articles;
import com.ara27.newsfeeder.domain.Data;
import com.ara27.newsfeeder.service.DetikService;
import com.ara27.newsfeeder.service.TirtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1")
public class NewsFeederController {

    @Autowired
    DetikService detikService;

    @Autowired
    TirtoService tirtoService;

    @GetMapping("/feedme")
    public ResponseEntity getAll() throws IOException {
        List<Articles> finalList = new ArrayList<>();
        finalList.addAll(tirtoService.allTirtoSelectedArticles());
        finalList.addAll(detikService.listOfDetikPopularNews());
        return new ResponseEntity<>(new Data(finalList), HttpStatus.OK);
    }
}
