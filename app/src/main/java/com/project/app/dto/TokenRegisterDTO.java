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

    @JsonProperty(value = "user_name")
    private String userName;
    private String password;
}
