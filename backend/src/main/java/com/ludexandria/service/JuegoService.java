package com.ludexandria.service;

import com.ludexandria.dao.JuegoDao;
import com.ludexandria.exception.ApiException;
import com.ludexandria.model.Juego;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JuegoService {
    private final JuegoDao juegoDao;

    public JuegoService(JuegoDao juegoDao) {
        this.juegoDao = juegoDao;
    }

    public List<Juego> buscar(String q, int page, int size) {
        int limit = Math.min(Math.max(size, 1), 50);
        int offset = Math.max(page, 0) * limit;
        return juegoDao.search(q, limit, offset);
    }

    public Juego porId(Long id) {
        return juegoDao.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Juego no encontrado"));
    }

    /** Seed de prueba mientras no haya IGDB */
    public Juego crearDemo(String nombre) {
        Juego j = new Juego();
        j.setNombre(nombre);
        j.setResumen("Juego de demostración");
        j.setNotaIgdb(8.0);
        j.setDuracionMinutos(120);
        j.setUrlPortada("https://placehold.co/300x400?text=" + nombre.replace(" ", "+"));
        j.setUltimaActualizacion(Instant.now());
        return juegoDao.insert(j);
    }
}
