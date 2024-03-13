package com.project.app.dto;

import com.project.app.classBase.UserBase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserForgotDTO extends UserBase {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
