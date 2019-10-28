package com.ara27.newsfeeder.service.impl;

import com.ara27.newsfeeder.entity.UserEntity;
import com.ara27.newsfeeder.repository.UserRepository;
import com.ara27.newsfeeder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        userEntity.setDtCreated(DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()));
        if (!emailAlreadyExists(emailAddress)) {
            userRepository.save(userEntity);
        }
    }

    @Override
    public void unsubscribeUser(String emailAddress) {
        userRepository.deleteAll(userRepository.findAllByEmailAddress(emailAddress));
    }

    private boolean emailAlreadyExists(String emailAddress) {
        List<UserEntity> entities = userRepository.findAllByEmailAddress(emailAddress);
        return entities != null && !entities.isEmpty();
    }
}
