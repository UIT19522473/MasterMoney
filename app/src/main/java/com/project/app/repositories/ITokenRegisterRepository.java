package com.project.app.repositories;

import com.project.app.models.TokenRegister;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ITokenRegisterRepository extends JpaRepository<TokenRegister, Long> {
    //    Optional<TokenRegister> findTokenRegisterByUserName (String name);
    Optional<TokenRegister> findTokenRegisterByEmail(String email);
}
