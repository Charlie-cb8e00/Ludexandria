package com.ludexandria.dao;

import com.ludexandria.model.Juego;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    public List<Juego> findByNombres(List<String> nombres) {
        if (nombres.isEmpty()) return List.of();
        String placeholders = String.join(",", Collections.nCopies(nombres.size(), "?"));
        String sql = "SELECT * FROM juego WHERE LOWER(nombre) IN (" + placeholders + ") ORDER BY id";
        Object[] nombresNormalizados = nombres.stream()
                .map(nombre -> nombre.toLowerCase(Locale.ROOT))
                .toArray();
        return jdbc.query(sql, mapper, nombresNormalizados);
    }

    public Optional<Juego> findByIgdbId(Long igdbId) {
        var list = jdbc.query("SELECT * FROM juego WHERE igdb_id = ?", mapper, igdbId);
        return list.stream().findFirst();
    }

    public List<Juego> insertAll(List<Juego> juegos) {
        if (juegos.isEmpty()) return List.of();
        String values = String.join(",", Collections.nCopies(juegos.size(), "(?,?,?,?,?,?,?,?)"));
        String sql = "INSERT INTO juego (igdb_id, nombre, resumen, fecha_salida, nota_igdb, "
                + "duracion_minutos, url_portada, ultima_actualizacion) VALUES " + values;
        List<Object> parameters = new ArrayList<>(juegos.size() * 8);
        for (Juego juego : juegos) {
            parameters.add(juego.getIgdbId());
            parameters.add(juego.getNombre());
            parameters.add(juego.getResumen());
            parameters.add(juego.getFechaSalida());
            parameters.add(juego.getNotaIgdb());
            parameters.add(juego.getDuracionMinutos());
            parameters.add(juego.getUrlPortada());
            parameters.add(juego.getUltimaActualizacion() == null
                    ? null : Timestamp.from(juego.getUltimaActualizacion()));
        }
        jdbc.update(sql, parameters.toArray());
        return findByNombres(juegos.stream().map(Juego::getNombre).toList());
    }

    public void updatePortadas(Map<Long, String> portadasPorJuego) {
        if (portadasPorJuego.isEmpty()) return;
        List<Object[]> parameters = portadasPorJuego.entrySet().stream()
                .map(entry -> new Object[]{entry.getValue(), entry.getKey()})
                .toList();
        jdbc.batchUpdate("UPDATE juego SET url_portada=? WHERE id=?", parameters);
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

    @Transactional
    public Juego saveOrUpdate(Juego incoming) {
        Optional<Juego> cached = incoming.getIgdbId() == null
                ? Optional.empty()
                : findByIgdbId(incoming.getIgdbId());
        if (cached.isEmpty()) {
            cached = findByNombreExacto(incoming.getNombre());
        }
        if (cached.isEmpty()) {
            return insert(incoming);
        }

        Juego juego = cached.get();
        if (juego.getIgdbId() == null && incoming.getIgdbId() != null) {
            juego.setIgdbId(incoming.getIgdbId());
        }
        if (incoming.getResumen() != null && !incoming.getResumen().isBlank()) {
            juego.setResumen(incoming.getResumen());
        }
        if (incoming.getFechaSalida() != null) {
            juego.setFechaSalida(incoming.getFechaSalida());
        }
        if (incoming.getNotaIgdb() != null) {
            juego.setNotaIgdb(incoming.getNotaIgdb());
        }
        if (incoming.getUrlPortada() != null && !incoming.getUrlPortada().isBlank()
                && (incoming.getIgdbId() != null || juego.getUrlPortada() == null
                || juego.getUrlPortada().isBlank())) {
            juego.setUrlPortada(incoming.getUrlPortada());
        }
        juego.setUltimaActualizacion(incoming.getUltimaActualizacion());
        update(juego);
        return juego;
    }

    public void update(Juego j) {
        jdbc.update(
            """
            UPDATE juego SET igdb_id=?, nombre=?, resumen=?, fecha_salida=?, nota_igdb=?,
              duracion_minutos=?, url_portada=?, ultima_actualizacion=?
            WHERE id=?
            """,
            j.getIgdbId(), j.getNombre(), j.getResumen(), j.getFechaSalida(), j.getNotaIgdb(),
            j.getDuracionMinutos(), j.getUrlPortada(),
            j.getUltimaActualizacion() == null ? null : Timestamp.from(j.getUltimaActualizacion()),
            j.getId()
        );
    }
}
