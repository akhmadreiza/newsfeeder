package com.ara27.newsfeeder.repository;

import com.ara27.newsfeeder.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    UserEntity findByEmailAddress(String emailAddress);
}
