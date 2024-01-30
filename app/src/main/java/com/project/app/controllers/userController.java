package com.project.app.controllers;

import com.project.app.dto.UserDTO;
import com.project.app.dto.UserRegisterDTO;
import com.project.app.response.CommonResponse;
import com.project.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class userController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserDTO userDTO) {
        try {
            String tokenLogin = userService.loginUser(userDTO);
            return ResponseEntity.ok(CommonResponse.builder()
                    .status(200)
                    .message("login successfully")
                    .data(tokenLogin)
                    .build());
        }catch (Exception e){
            return ResponseEntity.status(401).body(CommonResponse.builder()
                    .status(401)
                    .message("login failed")
                    .data("")
                    .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO userRegisterDTO) {
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
                    .data("tokenRegister")
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
}
