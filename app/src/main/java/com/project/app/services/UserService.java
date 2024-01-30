package com.project.app.services;

import com.project.app.components.JwtTokenUtil;
import com.project.app.dto.UserDTO;
import com.project.app.dto.UserRegisterDTO;
import com.project.app.models.Role;
import com.project.app.models.TokenRegister;
import com.project.app.models.User;
import com.project.app.repositories.IRoleRepository;
import com.project.app.repositories.ITokenRegisterRepository;
import com.project.app.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
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

    private final JwtTokenUtil jwtTokenUtil;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Override
    public String createUserWithToken(UserRegisterDTO userRegisterDTO) {

        Optional<User> existingUser = iUserRepository.findUsersByName(userRegisterDTO.getName());
        if (existingUser.isPresent()) throw new RuntimeException("User is already register");
        if (!userRegisterDTO.checkRePassword()) throw new RuntimeException("Re-Password is incorrect");

        String tokenRegister = jwtTokenUtil.generateTokenRegister(userRegisterDTO);

        Optional<TokenRegister> existingTokenRegister = iTokenRegisterRepository.findTokenRegisterByUserName(userRegisterDTO.getName());

        if (existingTokenRegister.isPresent()) {
            existingTokenRegister.get().setToken(tokenRegister);
            iTokenRegisterRepository.save(existingTokenRegister.get());
        } else {
            String encodedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
            iTokenRegisterRepository.save(TokenRegister.builder()
                    .userName(userRegisterDTO.getName())
                    .password(encodedPassword)
                    .token(tokenRegister)
                    .build());
        }
        return tokenRegister;
    }

    @Override
    public boolean confirmRegister(String token) {

        String exactUserName = jwtTokenUtil.exactUserName(token);
        Optional<TokenRegister> existingTokenRegister = iTokenRegisterRepository.findTokenRegisterByUserName(exactUserName);
        if (existingTokenRegister.isPresent() && jwtTokenUtil.validatedTokenRegister(token, existingTokenRegister.get())) {
            Optional<User> existingUser = iUserRepository.findUsersByName(exactUserName);
            if (existingUser.isPresent()) {
//                if find username has been register -> remove it from table register_token
                iTokenRegisterRepository.delete(existingTokenRegister.get());
                throw new RuntimeException("User is already register");
            } else {

               Optional <Role> existingRole = iRoleRepository.findById(1L);
               if(existingRole.isEmpty()) throw new RuntimeException("Cannot find Role");

                iUserRepository.save(User.builder()
                        .name(exactUserName)
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

        Optional <User> optionalUser = iUserRepository.findUsersByName(userDTO.getName());
        if (optionalUser.isEmpty()){
            throw new RuntimeException("Invalid user name / password");
        }

        User existingUser = optionalUser.get();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getName(),userDTO.getPassword(), existingUser.getAuthorities());

        authenticationManager.authenticate(authenticationToken);
        return jwtTokenUtil.generateToken(existingUser);
    }
}
