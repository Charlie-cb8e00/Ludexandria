package com.ludexandria.service;

import com.ludexandria.dao.BibliotecaDao;
import com.ludexandria.dto.RecomendacionRequest;
import com.ludexandria.model.BibliotecaItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Versión MVP: recomienda desde la biblioteca real del usuario.
 * Más adelante se sustituye/amplía con la llamada real a la IA.
 */
@Service
public class RecomendacionService {
    private final BibliotecaDao bibliotecaDao;

    public RecomendacionService(BibliotecaDao bibliotecaDao) {
        this.bibliotecaDao = bibliotecaDao;
    }

    public List<Map<String, Object>> recomendar(Long idUsuario, RecomendacionRequest req) {
        List<BibliotecaItem> pendientes = bibliotecaDao.findByUsuario(idUsuario, "pendiente");
        List<Map<String, Object>> out = new ArrayList<>();
        int tiempo = req.getTiempo() <= 0 ? 60 : req.getTiempo();

        for (BibliotecaItem item : pendientes) {
            Integer dur = item.getJuego() != null ? item.getJuego().getDuracionMinutos() : null;
            if (dur != null && dur > tiempo) continue;
            out.add(Map.of(
                    "juego", item.getJuego(),
                    "motivo", "Está en tu lista de pendientes y encaja con " + tiempo + " minutos"
                            + (req.getAnimo().isBlank() ? "" : " y ánimo \"" + req.getAnimo() + "\"")
                            + (req.getPlataforma().isBlank() ? "" : " en " + req.getPlataforma())
            ));
            if (out.size() >= 5) break;
        }
        return out;
    }
}
