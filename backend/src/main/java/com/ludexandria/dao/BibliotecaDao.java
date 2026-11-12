package com.ludexandria.dao;

import com.ludexandria.model.BibliotecaItem;
import com.ludexandria.model.Juego;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class BibliotecaDao {
    private final JdbcTemplate jdbc;

    private final RowMapper<BibliotecaItem> mapper = (rs, i) -> {
        BibliotecaItem b = new BibliotecaItem();
        b.setId(rs.getLong("id"));
        b.setIdUsuario(rs.getLong("id_usuario"));
        b.setIdJuego(rs.getLong("id_juego"));
        b.setEstado(rs.getString("estado"));
        double nota = rs.getDouble("nota_personal");
        if (!rs.wasNull()) b.setNotaPersonal(nota);
        double horas = rs.getDouble("horas_jugadas");
        if (!rs.wasNull()) b.setHorasJugadas(horas);
        Timestamp ts = rs.getTimestamp("fecha_anadido");
        if (ts != null) b.setFechaAnadido(ts.toInstant());

        Juego j = new Juego();
        j.setId(rs.getLong("j_id"));
        j.setNombre(rs.getString("j_nombre"));
        j.setResumen(rs.getString("j_resumen"));
        j.setUrlPortada(rs.getString("j_portada"));
        double n = rs.getDouble("j_nota");
        if (!rs.wasNull()) j.setNotaIgdb(n);
        int dur = rs.getInt("j_duracion");
        if (!rs.wasNull()) j.setDuracionMinutos(dur);
        b.setJuego(j);
        return b;
    };

    public BibliotecaDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<BibliotecaItem> findByUsuario(Long idUsuario, String estado) {
        if (estado != null && !estado.isBlank()) {
            return jdbc.query(
                """
                SELECT b.*, j.id j_id, j.nombre j_nombre, j.resumen j_resumen,
                       j.url_portada j_portada, j.nota_igdb j_nota, j.duracion_minutos j_duracion
                FROM biblioteca b JOIN juego j ON j.id = b.id_juego
                WHERE b.id_usuario = ? AND b.estado = ?
                ORDER BY b.fecha_anadido DESC
                """,
                mapper, idUsuario, estado
            );
        }
        return jdbc.query(
            """
            SELECT b.*, j.id j_id, j.nombre j_nombre, j.resumen j_resumen,
                   j.url_portada j_portada, j.nota_igdb j_nota, j.duracion_minutos j_duracion
            FROM biblioteca b JOIN juego j ON j.id = b.id_juego
            WHERE b.id_usuario = ?
            ORDER BY b.fecha_anadido DESC
            """,
            mapper, idUsuario
        );
    }

    public Optional<BibliotecaItem> findByIdAndUsuario(Long id, Long idUsuario) {
        var list = jdbc.query(
            """
            SELECT b.*, j.id j_id, j.nombre j_nombre, j.resumen j_resumen,
                   j.url_portada j_portada, j.nota_igdb j_nota, j.duracion_minutos j_duracion
            FROM biblioteca b JOIN juego j ON j.id = b.id_juego
            WHERE b.id = ? AND b.id_usuario = ?
            """,
            mapper, id, idUsuario
        );
        return list.stream().findFirst();
    }

    public BibliotecaItem insert(BibliotecaItem b) {
        jdbc.update(
            "INSERT INTO biblioteca (id_usuario, id_juego, estado, nota_personal, horas_jugadas) VALUES (?,?,?,?,?)",
            b.getIdUsuario(), b.getIdJuego(), b.getEstado(), b.getNotaPersonal(), b.getHorasJugadas()
        );
        Long id = jdbc.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        b.setId(id);
        return b;
    }

    public void update(BibliotecaItem b) {
        jdbc.update(
            "UPDATE biblioteca SET estado=?, nota_personal=?, horas_jugadas=? WHERE id=? AND id_usuario=?",
            b.getEstado(), b.getNotaPersonal(), b.getHorasJugadas(), b.getId(), b.getIdUsuario()
        );
    }

    public void delete(Long id, Long idUsuario) {
        jdbc.update("DELETE FROM biblioteca WHERE id=? AND id_usuario=?", id, idUsuario);
    }
}
