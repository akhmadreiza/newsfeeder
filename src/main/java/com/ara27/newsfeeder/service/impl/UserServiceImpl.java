package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.entity.UserEntity;
import com.ara27.newsfeeder.repository.UserRepository;
import com.ara27.newsfeeder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public List<UserEntity> getAllSubscriber() {
        return userRepository.findAll();
    }

    @Override
    public void subscribeUser(String emailAddress) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID().toString());
        userEntity.setEmailAddress(emailAddress);
        userRepository.save(userEntity);
    }
}
