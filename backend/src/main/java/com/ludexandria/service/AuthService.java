package com.ludexandria.service;

import com.ludexandria.dao.UsuarioDao;
import com.ludexandria.dto.AuthRequest;
import com.ludexandria.dto.AuthResponse;
import com.ludexandria.exception.ApiException;
import com.ludexandria.model.Usuario;
import com.ludexandria.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UsuarioDao usuarioDao;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(UsuarioDao usuarioDao, PasswordEncoder encoder, JwtService jwtService) {
        this.usuarioDao = usuarioDao;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public AuthResponse registro(AuthRequest req) {
        if (usuarioDao.findByEmail(req.getEmail()).isPresent()) {
            throw new ApiException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        Usuario u = new Usuario();
        u.setEmail(req.getEmail());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        u.setNombre(req.getNombre() == null || req.getNombre().isBlank() ? req.getEmail() : req.getNombre());
        u.setRol("USER");
        usuarioDao.insert(u);
        String token = jwtService.generate(u.getId(), u.getEmail(), u.getRol());
        return new AuthResponse(token, u.getEmail(), u.getNombre(), u.getRol());
    }

    public AuthResponse login(AuthRequest req) {
        Usuario u = usuarioDao.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));
        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }
        String token = jwtService.generate(u.getId(), u.getEmail(), u.getRol());
        return new AuthResponse(token, u.getEmail(), u.getNombre(), u.getRol());
    }
}
