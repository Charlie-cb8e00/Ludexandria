package com.ludexandria.controller;

import com.ludexandria.dto.BibliotecaRequest;
import com.ludexandria.model.BibliotecaItem;
import com.ludexandria.service.BibliotecaService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/biblioteca")
public class BibliotecaController {
    private final BibliotecaService bibliotecaService;

    public BibliotecaController(BibliotecaService bibliotecaService) {
        this.bibliotecaService = bibliotecaService;
    }

    private Long uid(Authentication auth) {
        return Long.valueOf(auth.getName());
    }

    @GetMapping
    public List<BibliotecaItem> listar(
            Authentication auth,
            @RequestParam(required = false) String estado) {
        return bibliotecaService.listar(uid(auth), estado);
    }

    @PostMapping
    public BibliotecaItem anadir(Authentication auth, @Valid @RequestBody BibliotecaRequest req) {
        return bibliotecaService.anadir(uid(auth), req);
    }

    @PutMapping("/{id}")
    public BibliotecaItem actualizar(Authentication auth, @PathVariable Long id,
                                     @RequestBody BibliotecaRequest req) {
        return bibliotecaService.actualizar(uid(auth), id, req);
    }

    @DeleteMapping("/{id}")
    public void eliminar(Authentication auth, @PathVariable Long id) {
        bibliotecaService.eliminar(uid(auth), id);
    }
}
