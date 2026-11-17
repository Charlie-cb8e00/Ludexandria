package com.ludexandria.service;

import com.ludexandria.dao.JuegoDao;
import com.ludexandria.dto.JuegoSearchResponse;
import com.ludexandria.exception.ApiException;
import com.ludexandria.model.Juego;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class JuegoService {
    private final JuegoDao juegoDao;
    private final IgdbService igdbService;
    private final SteamService steamService;

    public JuegoService(JuegoDao juegoDao, IgdbService igdbService, SteamService steamService) {
        this.juegoDao = juegoDao;
        this.igdbService = igdbService;
        this.steamService = steamService;
    }

    public List<Juego> buscar(String q, int page, int size) {
        return buscarCatalogo(q, page, size).juegos();
    }

    public JuegoSearchResponse buscarCatalogo(String q, int page, int size) {
        int pagina = Math.max(page, 0);
        int limit = Math.min(Math.max(size, 1), 50);
        long offset = (long) pagina * limit;
        List<String> advertencias = new ArrayList<>();
        if (q != null && !q.isBlank() && pagina == 0) {
            ProviderSearchResult resultadosIgdb = igdbService.buscar(q, limit);
            ProviderSearchResult resultadosSteam = steamService.buscar(q, limit);
            if (resultadosIgdb.advertencia() != null) advertencias.add(resultadosIgdb.advertencia());
            if (resultadosSteam.advertencia() != null) advertencias.add(resultadosSteam.advertencia());
        }
        List<Juego> juegos = juegoDao.search(q, limit, offset);
        return new JuegoSearchResponse(juegos, advertencias, pagina, limit, juegoDao.countSearch(q));
    }

    public Map<String, Boolean> fuentes() {
        return Map.of("steam", true, "igdb", igdbService.isConfigured());
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
