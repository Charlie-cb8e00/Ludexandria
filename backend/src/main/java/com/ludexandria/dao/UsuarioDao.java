package com.ludexandria.dao;

import com.ludexandria.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class UsuarioDao {
    private final JdbcTemplate jdbc;

    private final RowMapper<Usuario> mapper = (rs, i) -> {
        Usuario u = new Usuario();
        u.setId(rs.getLong("id"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setNombre(rs.getString("nombre"));
        u.setRol(rs.getString("rol"));
        Timestamp ts = rs.getTimestamp("fecha_alta");
        if (ts != null) u.setFechaAlta(ts.toInstant());
        return u;
    };

    public UsuarioDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Optional<Usuario> findByEmail(String email) {
        var list = jdbc.query("SELECT * FROM usuario WHERE email = ?", mapper, email);
        return list.stream().findFirst();
    }

    public Optional<Usuario> findById(Long id) {
        var list = jdbc.query("SELECT * FROM usuario WHERE id = ?", mapper, id);
        return list.stream().findFirst();
    }

    public Usuario insert(Usuario u) {
        jdbc.update(
            "INSERT INTO usuario (email, password_hash, nombre, rol) VALUES (?,?,?,?)",
            u.getEmail(), u.getPasswordHash(), u.getNombre(), u.getRol()
        );
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        u.setId(id);
        return u;
    }

    public void updateRol(Long id, String rol) {
        jdbc.update("UPDATE usuario SET rol = ? WHERE id = ?", rol, id);
    }
}
