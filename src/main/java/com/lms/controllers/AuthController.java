package com.lms.controllers;

import com.lms.dto.request.PasswordChangeDto;
import com.lms.dto.request.UserRequestDto;
import com.lms.dto.response.UserResponseDto;
import com.lms.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto dto) {
        UserResponseDto responseDto = userService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login() {

        return ResponseEntity.ok("Login successful");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody PasswordChangeDto dto) {
        userService.changePassword(dto);
        return ResponseEntity.ok("Password changed successfully");
    }
}