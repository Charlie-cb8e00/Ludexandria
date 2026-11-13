package com.ludexandria.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ludexandria.dao.JuegoDao;
import com.ludexandria.model.Juego;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SteamService {
    private static final Logger log = LoggerFactory.getLogger(SteamService.class);
    private static final String STORE_SEARCH_URL = "https://store.steampowered.com/api/storesearch/";
    private static final String APP_DETAILS_URL = "https://store.steampowered.com/api/appdetails/";

    private final JuegoDao juegoDao;
    private final RestTemplate restTemplate;

    public SteamService(JuegoDao juegoDao, RestTemplateBuilder restTemplateBuilder) {
        this.juegoDao = juegoDao;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(8))
                .build();
    }

    public ProviderSearchResult buscar(String texto, int limite) {
        if (texto == null || texto.isBlank()) return ProviderSearchResult.sinResultados();

        URI searchUri = UriComponentsBuilder.fromUriString(STORE_SEARCH_URL)
                .queryParam("term", texto.trim())
                .queryParam("l", "spanish")
                .queryParam("cc", "ES")
                .build()
                .encode()
                .toUri();

        JsonNode respuesta;
        try {
            respuesta = restTemplate.getForObject(searchUri, JsonNode.class);
        } catch (RestClientException e) {
            log.warn("No se pudo consultar la búsqueda de Steam; se continuará con otras fuentes.", e);
            return new ProviderSearchResult(List.of(), "No se pudo conectar con Steam; se muestran resultados de otras fuentes.");
        }

        JsonNode items = respuesta == null ? null : respuesta.path("items");
        if (items == null || !items.isArray()) {
            log.warn("Steam devolvió una respuesta vacía o con formato inesperado.");
            return new ProviderSearchResult(List.of(), "Steam devolvió una respuesta no válida.");
        }

        List<JsonNode> resultados = new ArrayList<>();
        int maxResultados = Math.min(Math.max(limite, 1), 10);
        for (JsonNode item : items) {
            String tipo = item.path("type").asText("game");
            long appId = item.path("id").asLong(item.path("appid").asLong(0));
            if (appId < 1 || !"game".equalsIgnoreCase(tipo)) continue;
            resultados.add(item);
            if (resultados.size() >= maxResultados) break;
        }
        if (resultados.isEmpty()) return ProviderSearchResult.sinResultados();

        JsonNode detalles = null;
        String advertencia = null;
        try {
            detalles = cargarDetalles(resultados);
        } catch (RestClientException e) {
            log.warn("Steam no devolvió detalles; se guardarán los títulos de la búsqueda.", e);
            advertencia = "Steam devolvió títulos, pero no se pudieron cargar sus detalles.";
        }
        List<Juego> juegos = new ArrayList<>();
        for (JsonNode item : resultados) {
            long appId = item.path("id").asLong(item.path("appid").asLong(0));
            JsonNode detalle = detalles == null ? null : detalles.path(Long.toString(appId)).path("data");
            if (detalle != null && !detalle.isObject()) detalle = null;

            Juego juego = new Juego();
            juego.setNombre(texto(detalle, "name", item.path("name").asText("")));
            if (juego.getNombre().isBlank()) continue;
            juego.setResumen(texto(detalle, "short_description", null));
            juego.setFechaSalida(parsearFecha(textoAnidado(detalle, "release_date", "date")));
            setUrlPortada(juego,
                    texto(detalle, "capsule_imagev5", null),
                    texto(detalle, "capsule_image", null),
                    item.path("logo").asText(null),
                    texto(detalle, "header_image", null)
            );
            juego.setUltimaActualizacion(Instant.now());
            juegos.add(juegoDao.saveOrUpdate(juego));
        }
        return new ProviderSearchResult(juegos, advertencia);
    }

    private JsonNode cargarDetalles(List<JsonNode> resultados) {
        String appIds = resultados.stream()
                .map(item -> Long.toString(item.path("id").asLong(item.path("appid").asLong(0))))
                .collect(Collectors.joining(","));
        URI detailsUri = UriComponentsBuilder.fromUriString(APP_DETAILS_URL)
                .queryParam("appids", appIds)
                .queryParam("cc", "ES")
                .queryParam("l", "spanish")
                .build()
                .encode()
                .toUri();
        return restTemplate.getForObject(detailsUri, JsonNode.class);
    }

    private String texto(JsonNode node, String field, String fallback) {
        if (node == null) return fallback;
        String value = node.path(field).asText("");
        return value.isBlank() ? fallback : value;
    }

    private String textoAnidado(JsonNode node, String parent, String field) {
        return texto(node == null ? null : node.path(parent), field, null);
    }

    private void setUrlPortada(Juego juego, String... urls) {
        for (String url : urls) {
            if (url != null && !url.isBlank()) {
                juego.setUrlPortada(url);
                return;
            }
        }
    }

    private LocalDate parsearFecha(String raw) {
        if (raw == null || raw.isBlank()) return null;
        List<Locale> locales = List.of(Locale.ENGLISH, Locale.forLanguageTag("es"));
        List<String> patterns = List.of(
                "d MMM, yyyy", "d MMM yyyy", "d MMMM, yyyy", "d MMMM yyyy",
                "MMM d, yyyy", "MMMM d, yyyy"
        );
        for (Locale locale : locales) {
            for (String pattern : patterns) {
                try {
                    return LocalDate.parse(raw.trim(), DateTimeFormatter.ofPattern(pattern, locale));
                } catch (DateTimeParseException ignored) {
                    // Prueba los formatos y locales habituales que devuelve Steam.
                }
            }
        }
        return null;
    }
}
