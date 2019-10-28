package com.ara27.newsfeeder.controller;

import com.ara27.newsfeeder.entity.UserEntity;
import com.ara27.newsfeeder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedme/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/subscribe")
    public ResponseEntity subscribeUser(@RequestParam(required = true) String emailAddress) {
        userService.subscribeUser(emailAddress);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/user-list")
    public List<UserEntity> listAllSubcriber() {
        return userService.getAllSubscriber();
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity unsubscribeUser(@RequestParam(required = true) String emailAddress) {
        userService.unsubscribeUser(emailAddress);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
