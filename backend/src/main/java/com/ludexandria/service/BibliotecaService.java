package com.ludexandria.service;

import com.ludexandria.dao.BibliotecaDao;
import com.ludexandria.dao.JuegoDao;
import com.ludexandria.dto.BibliotecaRequest;
import com.ludexandria.exception.ApiException;
import com.ludexandria.model.BibliotecaItem;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BibliotecaService {
    private final BibliotecaDao bibliotecaDao;
    private final JuegoDao juegoDao;

    public BibliotecaService(BibliotecaDao bibliotecaDao, JuegoDao juegoDao) {
        this.bibliotecaDao = bibliotecaDao;
        this.juegoDao = juegoDao;
    }

    public List<BibliotecaItem> listar(Long idUsuario, String estado) {
        return bibliotecaDao.findByUsuario(idUsuario, estado);
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
