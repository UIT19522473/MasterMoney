package com.project.app.repositories;

import com.project.app.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {
    //    Optional<User> findUsersByName(String name);
    Optional<User> findUsersByEmail(String email);
}
