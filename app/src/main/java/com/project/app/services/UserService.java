package com.project.app.services;

import com.project.app.components.JwtTokenUtil;
import com.project.app.dto.UserDTO;
import com.project.app.dto.UserForgotDTO;
import com.project.app.dto.UserRegisterDTO;
import com.project.app.models.Role;
import com.project.app.models.TokenRegister;
import com.project.app.models.User;
import com.project.app.models.UserForgot;
import com.project.app.repositories.IRoleRepository;
import com.project.app.repositories.ITokenRegisterRepository;
import com.project.app.repositories.IUserForgotRepository;
import com.project.app.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository iUserRepository;
    private final ITokenRegisterRepository iTokenRegisterRepository;
    private final IRoleRepository iRoleRepository;
    private final IUserForgotRepository iUserForgotRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
//    private final UserDetailsService userDetailsService;

    private final EmailService emailService;

    @Value("${client.url}")
    private String urlClient;

    @Override
    public String createUserWithToken(UserRegisterDTO userRegisterDTO) {

//        check user is existing in database -> If it is already register -> does not create user
        Optional<User> existingEmail = iUserRepository.findUsersByEmail(userRegisterDTO.getEmail());
        if (existingEmail.isPresent()) throw new RuntimeException("User is already register");

//        check password and retype password are matched. If match, process will continue
        if (!userRegisterDTO.checkRePassword()) throw new RuntimeException("Re-Password is incorrect");

//        generate token register
        String tokenRegister = jwtTokenUtil.generateTokenRegister(userRegisterDTO);

//        check token register is existing in database. If it has been exist -> update token, else create new token register in database
        Optional<TokenRegister> existingTokenRegister = iTokenRegisterRepository.findTokenRegisterByEmail(userRegisterDTO.getEmail());

//        If it has been exist -> update token
        if (existingTokenRegister.isPresent()) {
            existingTokenRegister.get().setToken(tokenRegister);
            iTokenRegisterRepository.save(existingTokenRegister.get());

//        else create new token register in database
        } else {
            String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
            iTokenRegisterRepository.save(TokenRegister.builder()
                    .name(userRegisterDTO.getName())
                    .email(userRegisterDTO.getEmail())
                    .password(encodedPassword)
                    .token(tokenRegister)
                    .build());
        }

//        send email to user, which is used to register
        String urlConfirmRegister = urlClient + "/confirm-register?token=" + tokenRegister;
        String subjectEmail = "Please click this link to complete registration: " + urlConfirmRegister;
//        emailService.sendEmail(userRegisterDTO.getEmail(),"Confirm Register Account",subjectEmail);
        emailService.sendEmail(userRegisterDTO.getEmail(), "Confirm Register Account", subjectEmail);
        return tokenRegister;
    }

    @Override
    public boolean confirmRegister(String token) {

        String exactEmail = jwtTokenUtil.exactEmail(token);

        Optional<TokenRegister> existingTokenRegister = iTokenRegisterRepository.findTokenRegisterByEmail(exactEmail);
        if (existingTokenRegister.isPresent() && jwtTokenUtil.validatedTokenRegister(token, existingTokenRegister.get())) {
            Optional<User> existingUser = iUserRepository.findUsersByEmail(exactEmail);
            if (existingUser.isPresent() && token.equals(existingTokenRegister.get().getToken())) {
//                if find username has been register -> remove it from table register_token
                iTokenRegisterRepository.delete(existingTokenRegister.get());
                throw new RuntimeException("User is already register");
            } else {

                Optional<Role> existingRole = iRoleRepository.findById(1L);
                if (existingRole.isEmpty()) throw new RuntimeException("Cannot find Role");

                if (jwtTokenUtil.isTokenExpired(token)) {
                    iTokenRegisterRepository.delete(existingTokenRegister.get());
                    throw new RuntimeException("The registration confirmation time has expired, please register again");
                }

//                save account change password
                iUserRepository.save(User.builder()
                        .name(existingTokenRegister.get().getName())
                        .email(exactEmail)
                        .password(existingTokenRegister.get().getPassword())
                        .role(existingRole.get())
                        .build());

                iTokenRegisterRepository.delete(existingTokenRegister.get());
                return true;
            }
        }

        return false;
    }

    @Override
    public String loginUser(UserDTO userDTO) {

        Optional<User> optionalUser = iUserRepository.findUsersByEmail(userDTO.getEmail());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Invalid user name / password");
        }

        User existingUser = optionalUser.get();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getEmail(), userDTO.getPassword(), existingUser.getAuthorities());

        authenticationManager.authenticate(authenticationToken);

        return jwtTokenUtil.generateToken(existingUser);
    }

    @Override
    public String createUserForgotToken(UserForgotDTO userForgotDTO) {
        Optional<User> existingUser = iUserRepository.findUsersByEmail(userForgotDTO.getEmail());
        if (existingUser.isEmpty())
            throw new RuntimeException("Cannot find user with email: " + userForgotDTO.getEmail());

        String tokenForgot = jwtTokenUtil.generateTokenForgot(userForgotDTO);

        Optional<UserForgot> existingUserForgot = iUserForgotRepository.findUserForgotByUserId_Id(existingUser.get().getId());

        if (existingUserForgot.isPresent()) {
            existingUserForgot.get().setToken(tokenForgot);
            iUserForgotRepository.save(existingUserForgot.get());
        } else {
            iUserForgotRepository.save(UserForgot.builder()
                    .token(tokenForgot)
                    .userId(existingUser.get())
                    .build());
        }
        String urlChangePassword = urlClient + "/change-password?token=" + tokenForgot;
        String subjectEmail = "Please click this link to complete change password: " + urlChangePassword;
//        emailService.sendEmail(userRegisterDTO.getEmail(),"Confirm Register Account",subjectEmail);
        emailService.sendEmail(userForgotDTO.getEmail(), "Confirm Change Password", subjectEmail);
        return tokenForgot;
    }

    @Override
    public boolean changePassword(String token, String password, String rePassword) {
        String exactEmail = jwtTokenUtil.exactEmail(token);

        Optional<User> existingUser = iUserRepository.findUsersByEmail(exactEmail);
        if (existingUser.isPresent() && jwtTokenUtil.validatedToken(token, existingUser.get())) {
            Optional<UserForgot> existingUserForgot = iUserForgotRepository.findUserForgotByUserId_Id(existingUser.get().getId());
            if (existingUserForgot.isPresent() && token.equals(existingUserForgot.get().getToken())) {
//                if find username has been forgot password -> remove it from table users_forgot
                if (password.equals(rePassword)) {

                    String encodedPassword = passwordEncoder.encode(password);

                    existingUser.get().setPassword(encodedPassword);
                    iUserRepository.save(existingUser.get());
                    iUserForgotRepository.delete(existingUserForgot.get());
                    return true;
                } else {
                    throw new RuntimeException("password does not match retype password");
                }
            }
        } else {
            throw new RuntimeException("token change password is not valid");
        }
        return false;
    }
}
