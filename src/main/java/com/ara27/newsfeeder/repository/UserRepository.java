package com.ara27.newsfeeder.repository;

import com.ara27.newsfeeder.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    List<UserEntity> findAllByEmailAddress(String emailAddress);

    List<UserEntity> findAllByUserType(String userType);
}
