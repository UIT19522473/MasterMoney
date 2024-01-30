package com.project.app.models;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "register_token")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;

    @Column(name = "user_name")
    private String userName;
    private String password;
}
