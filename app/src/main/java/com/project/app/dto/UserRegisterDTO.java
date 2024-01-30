package com.project.app.dto;

import com.project.app.classBase.UserBase;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO extends UserBase {
    private String name;
    private String password;
    private String re_password;

    public boolean checkRePassword(){
        return password.equals(re_password);
    }
}
