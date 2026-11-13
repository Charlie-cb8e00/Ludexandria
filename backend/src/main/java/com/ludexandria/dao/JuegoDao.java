package com.ludexandria.dao;

import com.ludexandria.model.Juego;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class JuegoDao {
    private final JdbcTemplate jdbc;

    private final RowMapper<Juego> mapper = (rs, i) -> {
        Juego j = new Juego();
        j.setId(rs.getLong("id"));
        long igdb = rs.getLong("igdb_id");
        if (!rs.wasNull()) j.setIgdbId(igdb);
        j.setNombre(rs.getString("nombre"));
        j.setResumen(rs.getString("resumen"));
        Date d = rs.getDate("fecha_salida");
        if (d != null) j.setFechaSalida(d.toLocalDate());
        double nota = rs.getDouble("nota_igdb");
        if (!rs.wasNull()) j.setNotaIgdb(nota);
        int dur = rs.getInt("duracion_minutos");
        if (!rs.wasNull()) j.setDuracionMinutos(dur);
        j.setUrlPortada(rs.getString("url_portada"));
        Timestamp ts = rs.getTimestamp("ultima_actualizacion");
        if (ts != null) j.setUltimaActualizacion(ts.toInstant());
        return j;
    };

    public JuegoDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Juego> search(String texto, int limit, int offset) {
        String q = "%" + (texto == null ? "" : texto.trim()) + "%";
        return jdbc.query(
            "SELECT * FROM juego WHERE nombre LIKE ? ORDER BY nombre LIMIT ? OFFSET ?",
            mapper, q, limit, offset
        );
    }

    public Optional<Juego> findById(Long id) {
        var list = jdbc.query("SELECT * FROM juego WHERE id = ?", mapper, id);
        return list.stream().findFirst();
    }

    public Optional<Juego> findByNombreExacto(String nombre) {
        var list = jdbc.query("SELECT * FROM juego WHERE LOWER(nombre) = LOWER(?) ORDER BY id LIMIT 1", mapper, nombre);
        return list.stream().findFirst();
    }

    public Optional<Juego> findByIgdbId(Long igdbId) {
        var list = jdbc.query("SELECT * FROM juego WHERE igdb_id = ?", mapper, igdbId);
        return list.stream().findFirst();
    }

    public Juego insert(Juego j) {
        jdbc.update(
            "INSERT INTO juego (igdb_id, nombre, resumen, fecha_salida, nota_igdb, duracion_minutos, url_portada, ultima_actualizacion) VALUES (?,?,?,?,?,?,?,?)",
            j.getIgdbId(), j.getNombre(), j.getResumen(),
            j.getFechaSalida(), j.getNotaIgdb(), j.getDuracionMinutos(),
            j.getUrlPortada(), j.getUltimaActualizacion() == null ? null : Timestamp.from(j.getUltimaActualizacion())
        );
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        j.setId(id);
        return j;
    }

    public void update(Juego j) {
        jdbc.update(
            """
            UPDATE juego SET nombre=?, resumen=?, fecha_salida=?, nota_igdb=?,
              duracion_minutos=?, url_portada=?, ultima_actualizacion=?
            WHERE id=?
            """,
            j.getNombre(), j.getResumen(), j.getFechaSalida(), j.getNotaIgdb(),
            j.getDuracionMinutos(), j.getUrlPortada(),
            j.getUltimaActualizacion() == null ? null : Timestamp.from(j.getUltimaActualizacion()),
            j.getId()
        );
    }
}
