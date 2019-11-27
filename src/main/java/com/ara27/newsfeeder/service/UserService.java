package com.ara27.newsfeeder.service;

import com.ara27.newsfeeder.entity.UserEntity;

import java.util.List;

public interface UserService {
    List<UserEntity> getAllSubscriber();

    void subscribeUser(String emailAddress, String name);

    void unsubscribeUser(String emailAddress);
}
