package com.ludexandria.service;

import com.ludexandria.dao.BibliotecaDao;
import com.ludexandria.dao.JuegoDao;
import com.ludexandria.dto.BibliotecaRequest;
import com.ludexandria.exception.ApiException;
import com.ludexandria.model.BibliotecaItem;
import com.ludexandria.model.Juego;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BibliotecaService {
    private final BibliotecaDao bibliotecaDao;
    private final JuegoDao juegoDao;
    private static final List<String> JUEGOS_POPULARES = List.of(
            "Minecraft",
            "The Witcher 3: Wild Hunt",
            "The Legend of Zelda: Breath of the Wild",
            "Red Dead Redemption 2",
            "Elden Ring",
            "Stardew Valley",
            "Hollow Knight",
            "Hades",
            "Celeste",
            "God of War Ragnarök"
    );

    public BibliotecaService(BibliotecaDao bibliotecaDao, JuegoDao juegoDao) {
        this.bibliotecaDao = bibliotecaDao;
        this.juegoDao = juegoDao;
    }

    public List<BibliotecaItem> listar(Long idUsuario, String estado) {
        return bibliotecaDao.findByUsuario(idUsuario, estado);
    }

    @Transactional
    public Map<String, Integer> anadirPopulares(Long idUsuario) {
        List<BibliotecaItem> bibliotecaActual = bibliotecaDao.findByUsuario(idUsuario, null);
        var idsEnBiblioteca = bibliotecaActual.stream()
                .map(BibliotecaItem::getIdJuego)
                .collect(Collectors.toSet());
        int anadidos = 0;
        int yaExistentes = 0;

        for (String nombre : JUEGOS_POPULARES) {
            var juego = juegoDao.findByNombreExacto(nombre).orElseGet(() -> {
                var nuevo = new Juego();
                nuevo.setNombre(nombre);
                nuevo.setUltimaActualizacion(Instant.now());
                return juegoDao.insert(nuevo);
            });

            if (!idsEnBiblioteca.add(juego.getId())) {
                yaExistentes++;
                continue;
            }

            BibliotecaItem item = new BibliotecaItem();
            item.setIdUsuario(idUsuario);
            item.setIdJuego(juego.getId());
            item.setEstado("pendiente");
            item.setHorasJugadas(0.0);
            bibliotecaDao.insert(item);
            anadidos++;
        }

        return Map.of("anadidos", anadidos, "yaExistentes", yaExistentes);
    }

    public BibliotecaItem anadir(Long idUsuario, BibliotecaRequest req) {
        juegoDao.findById(req.getIdJuego())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Juego no encontrado"));
        BibliotecaItem b = new BibliotecaItem();
        b.setIdUsuario(idUsuario);
        b.setIdJuego(req.getIdJuego());
        b.setEstado(req.getEstado() == null ? "pendiente" : req.getEstado());
        b.setNotaPersonal(req.getNotaPersonal());
        b.setHorasJugadas(req.getHorasJugadas() == null ? 0.0 : req.getHorasJugadas());
        return bibliotecaDao.insert(b);
    }

    public BibliotecaItem actualizar(Long idUsuario, Long id, BibliotecaRequest req) {
        BibliotecaItem b = bibliotecaDao.findByIdAndUsuario(id, idUsuario)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Entrada no encontrada"));
        if (req.getEstado() != null) b.setEstado(req.getEstado());
        if (req.getNotaPersonal() != null) b.setNotaPersonal(req.getNotaPersonal());
        if (req.getHorasJugadas() != null) b.setHorasJugadas(req.getHorasJugadas());
        bibliotecaDao.update(b);
        return bibliotecaDao.findByIdAndUsuario(id, idUsuario).orElse(b);
    }

    public void eliminar(Long idUsuario, Long id) {
        bibliotecaDao.findByIdAndUsuario(id, idUsuario)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Entrada no encontrada"));
        bibliotecaDao.delete(id, idUsuario);
    }
}
