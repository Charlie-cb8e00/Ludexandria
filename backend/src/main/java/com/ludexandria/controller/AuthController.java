package com.ludexandria.controller;

import com.ludexandria.dto.AuthRequest;
import com.ludexandria.dto.AuthResponse;
import com.ludexandria.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public AuthResponse registro(@Valid @RequestBody AuthRequest req) {
        return authService.registro(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest req) {
        return authService.login(req);
    }
}
