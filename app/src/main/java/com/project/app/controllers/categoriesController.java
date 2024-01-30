package com.project.app.controllers;

import com.project.app.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/test")
@RequiredArgsConstructor
public class categoriesController {
    @GetMapping("")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok(CommonResponse.builder()
                .status(200)
                .message("hello")
                .data("this is data")
                .build());
    }
}

