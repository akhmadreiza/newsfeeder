package com.ara27.newsfeeder.controller;

import com.ara27.newsfeeder.domain.Data;
import com.ara27.newsfeeder.service.DetikService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/ngumpuli/v1")
public class DetikController {

    @Autowired
    DetikService detikService;

    @GetMapping("/detik")
    public ResponseEntity getDetikMostPopular() throws IOException {
        return new ResponseEntity<>(new Data(detikService.listOfDetikPopularNews()), HttpStatus.OK);
    }
}
