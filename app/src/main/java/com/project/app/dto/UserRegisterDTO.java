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
public class UserRegisterDTO extends UserBase {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Name is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Password is required")
    private String re_password;

    public boolean checkRePassword(){
        return password.equals(re_password);
    }
}
