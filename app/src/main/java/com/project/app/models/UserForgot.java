package com.project.app.models;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "users_forgot")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForgot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;
}
