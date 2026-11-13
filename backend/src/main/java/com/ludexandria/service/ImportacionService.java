package com.ludexandria.service;

import com.ludexandria.dao.BibliotecaDao;
import com.ludexandria.dao.JuegoDao;
import com.ludexandria.exception.ApiException;
import com.ludexandria.model.BibliotecaItem;
import com.ludexandria.model.Juego;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;

@Service
public class ImportacionService {

    private static final Set<String> ESTADOS = Set.of(
            "pendiente", "jugando", "completado", "abandonado"
    );

    private final JuegoDao juegoDao;
    private final BibliotecaDao bibliotecaDao;

    public ImportacionService(JuegoDao juegoDao, BibliotecaDao bibliotecaDao) {
        this.juegoDao = juegoDao;
        this.bibliotecaDao = bibliotecaDao;
    }

    public Map<String, Object> importarCsv(Long idUsuario, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Archivo vacío");
        }
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase();
        if (!name.endsWith(".csv")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Solo se admiten archivos CSV");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El archivo supera 5 MB");
        }

        int importados = 0;
        int existentes = 0;
        int fallidos = 0;
        List<String> errores = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            boolean first = true;
            int linea = 0;

            while ((line = br.readLine()) != null) {
                linea++;
                line = line.trim();
                if (line.isEmpty()) continue;

                if (first) {
                    first = false;
                    String low = line.toLowerCase();
                    if (low.contains("nombre") && (low.contains("estado") || low.contains("plataforma"))) {
                        continue;
                    }
                }

                String[] cols = parseCsvLine(line);
                if (cols.length < 1 || cols[0].isBlank()) {
                    fallidos++;
                    errores.add("Línea " + linea + ": nombre vacío");
                    continue;
                }

                String nombre = cols[0].trim();
                String plataforma = cols.length > 1 ? cols[1].trim() : "";
                String estado = cols.length > 2 ? cols[2].trim().toLowerCase() : "pendiente";
                Double nota = null;
                Double horas = 0.0;

                if (cols.length > 3 && !cols[3].isBlank()) {
                    try {
                        nota = Double.parseDouble(cols[3].replace(",", "."));
                    } catch (NumberFormatException e) { /* ignorar */ }
                }
                if (cols.length > 4 && !cols[4].isBlank()) {
                    try {
                        horas = Double.parseDouble(cols[4].replace(",", "."));
                    } catch (NumberFormatException e) { /* ignorar */ }
                }

                if (!ESTADOS.contains(estado)) {
                    estado = "pendiente";
                }

                try {
                    Juego encontrado = juegoDao.findByNombreExacto(nombre).orElse(null);

                    final Juego juego;
                    if (encontrado == null) {
                        Juego nuevo = new Juego();
                        nuevo.setNombre(nombre);
                        nuevo.setResumen(plataforma.isBlank() ? null : "Plataforma: " + plataforma);
                        nuevo.setUltimaActualizacion(Instant.now());
                        juego = juegoDao.insert(nuevo);
                    } else {
                        juego = encontrado;
                    }

                    final Long idJuego = juego.getId();

                    boolean yaExiste = bibliotecaDao.findByUsuario(idUsuario, null).stream()
                            .anyMatch(b -> b.getIdJuego().equals(idJuego));

                    if (yaExiste) {
                        existentes++;
                        continue;
                    }

                    BibliotecaItem item = new BibliotecaItem();
                    item.setIdUsuario(idUsuario);
                    item.setIdJuego(idJuego);
                    item.setEstado(estado);
                    item.setNotaPersonal(nota);
                    item.setHorasJugadas(horas);
                    bibliotecaDao.insert(item);
                    importados++;
                } catch (Exception e) {
                    fallidos++;
                    errores.add("Línea " + linea + ": " + e.getMessage());
                }
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "No se pudo leer el CSV: " + e.getMessage());
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("importados", importados);
        res.put("yaExistentes", existentes);
        res.put("fallidos", fallidos);
        res.put("errores", errores);
        return res;
    }

    private String[] parseCsvLine(String line) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;
        // Una coma separa columnas solo si está fuera de comillas.
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                out.add(cur.toString().trim());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        out.add(cur.toString().trim());
        return out.toArray(new String[0]);
    }
}
