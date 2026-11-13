package com.ludexandria.controller;

import com.ludexandria.dto.JuegoSearchResponse;
import com.ludexandria.model.Juego;
import com.ludexandria.service.JuegoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/juegos")
public class JuegoController {
    private final JuegoService juegoService;

    public JuegoController(JuegoService juegoService) {
        this.juegoService = juegoService;
    }

    @GetMapping
    public List<Juego> buscar(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return juegoService.buscar(q, page, size);
    }

    @GetMapping("/buscar")
    public JuegoSearchResponse buscarCatalogo(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "20") int size) {
        return juegoService.buscarCatalogo(q, page, size);
    }

    @GetMapping("/fuentes")
    public Map<String, Boolean> fuentes() {
        return juegoService.fuentes();
    }

    @GetMapping("/{id}")
    public Juego detalle(@PathVariable Long id) {
        return juegoService.porId(id);
    }

    /** Solo para pruebas locales sin IGDB */
    @PostMapping("/demo")
    public Juego crearDemo(@RequestBody Map<String, String> body) {
        return juegoService.crearDemo(body.getOrDefault("nombre", "Juego demo"));
    }
}
