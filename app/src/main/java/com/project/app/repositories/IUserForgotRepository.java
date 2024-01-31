package com.project.app.repositories;

import com.project.app.models.UserForgot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserForgotRepository extends JpaRepository<UserForgot, Long> {
    Optional<UserForgot> findUserForgotByUserId_Id(Long userId);
}
