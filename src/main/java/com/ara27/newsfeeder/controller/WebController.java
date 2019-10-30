package com.ara27.newsfeeder.controller;

import com.ara27.newsfeeder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/feedme/user")
public class WebController {

    @Autowired
    UserService userService;

    @GetMapping("/subscribe")
    public String postSubscribeHandler(Model model, @RequestParam(required = true) String emailAddress) {
        ResponseEntity responseEntity = subscribeUser(emailAddress);
        if (responseEntity.getStatusCode().equals(HttpStatus.CREATED)) {
            model.addAttribute("subsHeadMessage", "Halo!");
            model.addAttribute("subsContentMessage1", "Terima kasih telah berlangganan layanan kami.");
            model.addAttribute("subsContentMessage2", "Tunggu update berita dan artikel populer dari kami, ya!");
        } else {
            model.addAttribute("subsHeadMessage", "Ups..");
            model.addAttribute("subsContentMessage1", "Layanan ini sementara tidak tersedia.");
            model.addAttribute("subsContentMessage2", "Mohon maaf atas ketidaknyamanannya, ya!");
        }
        return "home-subs";
    }

    private ResponseEntity subscribeUser(@RequestParam(required = true) String emailAddress) {
        userService.subscribeUser(emailAddress);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/unsubscribe")
    public String postUnsubscribeHandler(Model model, @RequestParam(required = true) String emailAddress) {
        ResponseEntity responseEntity = unsubscribeUser(emailAddress);
        if (responseEntity.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            model.addAttribute("subsHeadMessage", "Sayonara..");
            model.addAttribute("subsContentMessage1", "Kamu telah berhasil berhenti berlangganan layanan kami..");
            model.addAttribute("subsContentMessage2", "Sampai jumpa lagi..");
        } else {
            model.addAttribute("subsHeadMessage", "Ups..");
            model.addAttribute("subsContentMessage1", "Layanan ini sementara tidak tersedia.");
            model.addAttribute("subsContentMessage2", "Mohon maaf atas ketidaknyamanannya, ya!");
        }
        return "home-subs";
    }

    public ResponseEntity unsubscribeUser(@RequestParam(required = true) String emailAddress) {
        userService.unsubscribeUser(emailAddress);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
