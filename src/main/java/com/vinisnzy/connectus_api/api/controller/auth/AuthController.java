package com.vinisnzy.connectus_api.api.controller.auth;

import com.vinisnzy.connectus_api.infra.security.dto.request.CreateUserRequest;
import com.vinisnzy.connectus_api.infra.security.service.AuthService;
import com.vinisnzy.connectus_api.infra.security.dto.request.LoginRequest;
import com.vinisnzy.connectus_api.infra.security.dto.response.LoginResponse;
import com.vinisnzy.connectus_api.infra.security.dto.request.RegisterUserRequest;
import com.vinisnzy.connectus_api.infra.security.dto.response.RegisterUserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = service.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        RegisterUserResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/create-user")
    @PreAuthorize("@permissionEvaluator.hasPermission(authentication, 'users', 'create')")
    public ResponseEntity<RegisterUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        RegisterUserResponse response = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
