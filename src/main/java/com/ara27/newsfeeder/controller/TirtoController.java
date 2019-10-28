package com.ara27.newsfeeder.controller;

import com.ara27.newsfeeder.domain.Data;
import com.ara27.newsfeeder.service.TirtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/feedme/v1")
public class TirtoController {
    @Autowired
    TirtoService tirtoService;

    @GetMapping("/tirto")
    public ResponseEntity getTirtoPopular() throws IOException {
        return new ResponseEntity<>(new Data(tirtoService.popularTirtoArticles()), HttpStatus.OK);
    }
}
