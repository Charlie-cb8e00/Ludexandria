package com.ludexandria.controller;

import com.ludexandria.dao.UsuarioDao;
import com.ludexandria.exception.ApiException;
import com.ludexandria.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UsuarioDao usuarioDao;
    private final JdbcTemplate jdbc;

    public AdminController(UsuarioDao usuarioDao, JdbcTemplate jdbc) {
        this.usuarioDao = usuarioDao;
        this.jdbc = jdbc;
    }

    @GetMapping("/usuarios")
    public List<Map<String, Object>> listarUsuarios() {
        return jdbc.query(
            "SELECT id, email, nombre, rol, fecha_alta FROM usuario ORDER BY id",
            (rs, i) -> Map.of(
                "id", rs.getLong("id"),
                "email", rs.getString("email"),
                "nombre", rs.getString("nombre"),
                "rol", rs.getString("rol"),
                "fechaAlta", rs.getTimestamp("fecha_alta").toInstant().toString()
            )
        );
    }

    @PutMapping("/usuarios/{id}/rol")
    public Map<String, Object> cambiarRol(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String rol = body.getOrDefault("rol", "").toUpperCase();
        if (!rol.equals("USER") && !rol.equals("ADMIN")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Rol debe ser USER o ADMIN");
        }
        Usuario u = usuarioDao.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        jdbc.update("UPDATE usuario SET rol = ? WHERE id = ?", rol, id);
        return Map.of("id", id, "email", u.getEmail(), "rol", rol);
    }

    @GetMapping("/consumo-ia")
    public List<Map<String, Object>> consumoIa() {
        return jdbc.query(
            """
            SELECT c.id_usuario, u.email, c.fecha, c.tokens
            FROM consumo_ia c
            JOIN usuario u ON u.id = c.id_usuario
            ORDER BY c.fecha DESC
            LIMIT 200
            """,
            (rs, i) -> Map.of(
                "idUsuario", rs.getLong("id_usuario"),
                "email", rs.getString("email"),
                "fecha", rs.getDate("fecha").toString(),
                "tokens", rs.getInt("tokens")
            )
        );
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        Integer usuarios = jdbc.queryForObject("SELECT COUNT(*) FROM usuario", Integer.class);
        Integer juegos = jdbc.queryForObject("SELECT COUNT(*) FROM juego", Integer.class);
        Integer entradas = jdbc.queryForObject("SELECT COUNT(*) FROM biblioteca", Integer.class);
        return Map.of(
            "usuarios", usuarios == null ? 0 : usuarios,
            "juegos", juegos == null ? 0 : juegos,
            "entradasBiblioteca", entradas == null ? 0 : entradas
        );
    }
}
