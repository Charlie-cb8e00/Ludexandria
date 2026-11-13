package com.ludexandria.service;

import com.ludexandria.dao.JuegoDao;
import com.ludexandria.dto.JuegoSearchResponse;
import com.ludexandria.exception.ApiException;
import com.ludexandria.model.Juego;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
        int limit = Math.min(Math.max(size, 1), 50);
        int offset = Math.max(page, 0) * limit;
        // Las páginas siguientes usan la caché local; las fuentes remotas solo se consultan al buscar.
        if (q == null || q.isBlank() || page > 0) {
            return new JuegoSearchResponse(juegoDao.search(q, limit, offset), List.of());
        }

        List<Juego> resultados = new ArrayList<>();
        List<String> advertencias = new ArrayList<>();
        ProviderSearchResult resultadosIgdb = igdbService.buscar(q, limit);
        ProviderSearchResult resultadosSteam = steamService.buscar(q, limit);
        resultados.addAll(resultadosIgdb.juegos());
        resultados.addAll(resultadosSteam.juegos());
        resultados.addAll(juegoDao.search(q, 50, 0));
        if (resultadosIgdb.advertencia() != null) advertencias.add(resultadosIgdb.advertencia());
        if (resultadosSteam.advertencia() != null) advertencias.add(resultadosSteam.advertencia());

        LinkedHashMap<Long, Juego> unicos = new LinkedHashMap<>();
        for (Juego juego : resultados) {
            if (juego.getId() != null) unicos.putIfAbsent(juego.getId(), juego);
        }
        return new JuegoSearchResponse(unicos.values().stream().limit(limit).toList(), advertencias);
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
