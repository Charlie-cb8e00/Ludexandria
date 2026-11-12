package com.ludexandria.service;

import com.ludexandria.dao.JuegoDao;
import com.ludexandria.model.Juego;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Integración con IGDB (vía Twitch Client Credentials).
 * Si no hay client-id/secret configurados, las búsquedas usan solo la caché local.
 * Documentación: https://api-docs.igdb.com/
 */
@Service
public class IgdbService {

    private static final Logger log = LoggerFactory.getLogger(IgdbService.class);

    private final JuegoDao juegoDao;

    @Value("${app.igdb.client-id:}")
    private String clientId;

    @Value("${app.igdb.client-secret:}")
    private String clientSecret;

    public IgdbService(JuegoDao juegoDao) {
        this.juegoDao = juegoDao;
    }

    public boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    /**
     * Busca juegos. Con claves IGDB se ampliará la consulta remota;
     * sin claves usa solo la base local (MVP).
     */
    public List<Juego> buscar(String texto, int limit, int offset) {
        List<Juego> locales = juegoDao.search(texto, limit, offset);
        if (!isConfigured()) {
            return locales;
        }
        // TODO: llamada real a https://api.igdb.com/v4/games
        // 1) Obtener token Twitch (client_credentials)
        // 2) POST con body: search "texto"; fields name,summary,rating,...;
        // 3) Upsert en tabla juego por igdb_id
        log.debug("IGDB configurado; búsqueda remota pendiente de implementar. Devolviendo caché local.");
        return locales;
    }

    public Optional<Juego> porIgdbId(Long igdbId) {
        Optional<Juego> local = juegoDao.findByIgdbId(igdbId);
        if (local.isPresent() || !isConfigured()) {
            return local;
        }
        // TODO: fetch remoto y cachear
        return Optional.empty();
    }

    /** Crea un juego de demostración (útil sin IGDB). */
    public Juego crearLocal(String nombre, String resumen) {
        Juego j = new Juego();
        j.setNombre(nombre);
        j.setResumen(resumen);
        j.setUltimaActualizacion(Instant.now());
        return juegoDao.insert(j);
    }
}
