package com.project.app.controllers;

import com.project.app.dto.ChangePasswordDTO;
import com.project.app.dto.UserDTO;
import com.project.app.dto.UserForgotDTO;
import com.project.app.dto.UserRegisterDTO;
import com.project.app.response.CommonResponse;
import com.project.app.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*") // Đặt "*" để cho phép từ tất cả các origin
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class userController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO userDTO, BindingResult result) {

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(CommonResponse.builder()
                    .status(400)
                    .message("Login failed")
                    .data(errorMessages)
                    .build());
        }

        try {
            String tokenLogin = userService.loginUser(userDTO);
            return ResponseEntity.ok(CommonResponse.builder()
                    .status(200)
                    .message("login successfully")
                    .data(tokenLogin)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(401).body(CommonResponse.builder()
                    .status(401)
                    .message("login failed")
                    .data("")
                    .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO userRegisterDTO, BindingResult result) {

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(CommonResponse.builder()
                    .status(400)
                    .message("Register failed")
                    .data(errorMessages)
                    .build());
        }

        try {
            String tokenRegister = userService.createUserWithToken(userRegisterDTO);
            return ResponseEntity.ok(CommonResponse.builder()
                    .status(200)
                    .message("create register token successfully")
                    .data(tokenRegister)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.builder()
                    .status(409)
                    .message(e.getMessage())
                    .data("")
                    .build());
        }
    }


    @GetMapping("/confirm-register")
    public ResponseEntity<?> confirmRegister(@RequestParam String token) {

        if (userService.confirmRegister(token)) {
            return ResponseEntity.ok(CommonResponse.builder()
                    .status(200)
                    .message("confirm register successfully")
                    .data("")
                    .build());
        } else {
            return ResponseEntity.badRequest().body(CommonResponse.builder()
                    .status(500)
                    .message("Cannot register, because token register is not valid")
                    .data("")
                    .build());
        }

    }


//    change password

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@Valid @RequestBody UserForgotDTO userForgotDTO, BindingResult result) {

        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList();
            return ResponseEntity.badRequest().body(CommonResponse.builder()
                    .status(400)
                    .message("Forgot Password failed")
                    .data(errorMessages)
                    .build());
        }

        try {
            String tokenForgot = userService.createUserForgotToken(userForgotDTO);
            return ResponseEntity.ok(CommonResponse.builder()
                    .status(200)
                    .message("create forgot token successfully")
                    .data(tokenForgot)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(CommonResponse.builder()
                    .status(409)
                    .message(e.getMessage())
                    .data("")
                    .build());
        }
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String token,  @RequestBody ChangePasswordDTO changePasswordDTO) {

        if (userService.changePassword(token,changePasswordDTO.getPassword(),changePasswordDTO.getRePassword())) {
            return ResponseEntity.ok(CommonResponse.builder()
                    .status(200)
                    .message("change password successfully")
                    .data("")
                    .build());
        } else {
            return ResponseEntity.badRequest().body(CommonResponse.builder()
                    .status(500)
                    .message("Cannot change password, because token change password is not valid")
                    .data("")
                    .build());
        }
    }

    @GetMapping("/test")
    public ResponseEntity<?> testApi(){
        return ResponseEntity.ok("test oke");
    }

}
