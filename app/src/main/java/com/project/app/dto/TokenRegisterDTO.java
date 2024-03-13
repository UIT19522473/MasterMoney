package com.project.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRegisterDTO {
    private String token;

    private String name;
    private String email;
    private String password;
}
