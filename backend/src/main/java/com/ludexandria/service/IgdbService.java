package com.ludexandria.service;

import com.ludexandria.dao.JuegoDao;
import com.ludexandria.model.Juego;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Integración con IGDB (vía Twitch Client Credentials).
 * Documentación: https://api-docs.igdb.com/
 */
@Service
public class IgdbService {

    private static final Logger log = LoggerFactory.getLogger(IgdbService.class);
    private static final String TOKEN_URL = "https://id.twitch.tv/oauth2/token";
    private static final String GAMES_URL = "https://api.igdb.com/v4/games";

    private final JuegoDao juegoDao;
    private final RestTemplate restTemplate;
    private String accessToken;
    private Instant tokenExpiresAt = Instant.EPOCH;

    @Value("${app.igdb.client-id:}")
    private String clientId;

    @Value("${app.igdb.client-secret:}")
    private String clientSecret;

    public IgdbService(JuegoDao juegoDao, RestTemplateBuilder restTemplateBuilder) {
        this.juegoDao = juegoDao;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(8))
                .build();
    }

    public boolean isConfigured() {
        return clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank();
    }

    public ProviderSearchResult buscar(String texto, int limit) {
        if (!isConfigured() || texto == null || texto.isBlank()) return ProviderSearchResult.sinResultados();

        JsonNode respuesta;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Client-ID", clientId);
            headers.setBearerAuth(obtenerToken());
            headers.setContentType(MediaType.TEXT_PLAIN);
            String consulta = "search \"" + escaparConsulta(texto) + "\"; "
                    + "fields name,summary,first_release_date,rating,cover.url; "
                    + "limit " + Math.min(Math.max(limit, 1), 20) + ";";
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    GAMES_URL, HttpMethod.POST, new HttpEntity<>(consulta, headers), JsonNode.class);
            respuesta = response.getBody();
        } catch (RestClientException | IllegalStateException e) {
            log.warn("No se pudo consultar IGDB; se continuará con otras fuentes.", e);
            return new ProviderSearchResult(List.of(), "IGDB no está disponible; se muestran resultados de otras fuentes.");
        }

        if (respuesta == null || !respuesta.isArray()) {
            log.warn("IGDB devolvió una respuesta vacía o con formato inesperado.");
            return new ProviderSearchResult(List.of(), "IGDB devolvió una respuesta no válida.");
        }

        List<Juego> juegos = new ArrayList<>();
        for (JsonNode dato : respuesta) {
            String nombre = dato.path("name").asText("");
            if (nombre.isBlank() || !dato.path("id").canConvertToLong()) continue;

            Juego juego = new Juego();
            juego.setIgdbId(dato.path("id").asLong());
            juego.setNombre(nombre);
            if (dato.path("summary").isTextual()) juego.setResumen(dato.path("summary").asText());
            if (dato.path("rating").isNumber()) juego.setNotaIgdb(dato.path("rating").asDouble());
            if (dato.path("first_release_date").canConvertToLong()) {
                juego.setFechaSalida(Instant.ofEpochSecond(dato.path("first_release_date").asLong())
                        .atZone(ZoneOffset.UTC).toLocalDate());
            }
            String portada = dato.path("cover").path("url").asText("");
            if (!portada.isBlank()) {
                if (portada.startsWith("//")) portada = "https:" + portada;
                juego.setUrlPortada(portada.replace("t_thumb", "t_cover_big"));
            }
            juego.setUltimaActualizacion(Instant.now());
            juegos.add(juegoDao.saveOrUpdate(juego));
        }
        return new ProviderSearchResult(juegos, null);
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

    private synchronized String obtenerToken() {
        if (accessToken != null && Instant.now().isBefore(tokenExpiresAt)) return accessToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", "client_credentials");

        JsonNode respuesta = restTemplate.postForObject(
                TOKEN_URL, new HttpEntity<>(form, headers), JsonNode.class);
        if (respuesta == null || !respuesta.path("access_token").isTextual()) {
            throw new IllegalStateException("Twitch no devolvió un token de acceso para IGDB.");
        }
        accessToken = respuesta.path("access_token").asText();
        long segundos = Math.max(1, respuesta.path("expires_in").asLong(3600) - 60);
        tokenExpiresAt = Instant.now().plusSeconds(segundos);
        return accessToken;
    }

    private String escaparConsulta(String texto) {
        return texto.trim().replaceAll("[\\r\\n\\t]", " ")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
