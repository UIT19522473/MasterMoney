package com.project.app.services;

import com.project.app.dto.UserDTO;
import com.project.app.dto.UserForgotDTO;
import com.project.app.dto.UserRegisterDTO;
import com.project.app.models.User;

public interface IUserService {
    public String createUserWithToken (UserRegisterDTO userRegisterDTO);
    public boolean confirmRegister (String token);

    public String loginUser (UserDTO userDTO);

    public String createUserForgotToken (UserForgotDTO userForgotDTO);
    public boolean changePassword (String token, String password, String rePassword);

}
