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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class BibliotecaService {
    private final BibliotecaDao bibliotecaDao;
    private final JuegoDao juegoDao;
    private record JuegoPopular(String nombre, String urlPortada) {
    }

    private static JuegoPopular steam(String nombre, long appId) {
        return new JuegoPopular(nombre,
                "https://cdn.akamai.steamstatic.com/steam/apps/" + appId + "/library_600x900.jpg");
    }

    private static final List<JuegoPopular> JUEGOS_POPULARES = List.of(
            new JuegoPopular("Minecraft", null),
            steam("The Witcher 3: Wild Hunt", 292030),
            new JuegoPopular("The Legend of Zelda: Breath of the Wild", null),
            steam("Red Dead Redemption 2", 1174180),
            steam("Elden Ring", 1245620),
            steam("Stardew Valley", 413150),
            steam("Hollow Knight", 367520),
            steam("Hades", 1145360),
            steam("Celeste", 504230),
            steam("God of War Ragnarök", 2322010),
            steam("Cyberpunk 2077", 1091500),
            steam("Baldur's Gate 3", 1086940),
            steam("Grand Theft Auto V", 271590),
            steam("Portal 2", 620),
            steam("Counter-Strike 2", 730),
            steam("Terraria", 105600),
            steam("Dead Cells", 588650),
            steam("Cuphead", 268910),
            steam("Undertale", 391540),
            steam("Slay the Spire", 646570),
            steam("Subnautica", 264710),
            steam("Valheim", 892970),
            steam("No Man's Sky", 275850),
            steam("RimWorld", 294100),
            steam("Sid Meier's Civilization VI", 289070),
            steam("Resident Evil 4", 2050650),
            steam("Monster Hunter: World", 582010),
            steam("HELLDIVERS 2", 553850),
            steam("Palworld", 1623730),
            steam("Among Us", 945360),
            steam("Phasmophobia", 739630),
            steam("Lethal Company", 1966720),
            steam("Factorio", 427520),
            steam("Project Zomboid", 108600),
            steam("Disco Elysium - The Final Cut", 632470),
            steam("Fallout 4", 377160),
            steam("The Elder Scrolls V: Skyrim Special Edition", 489830),
            steam("DOOM Eternal", 782330),
            steam("Forza Horizon 5", 1551360),
            steam("Sea of Thieves", 1172620),
            steam("Lies of P", 1627720),
            steam("Balatro", 2379780),
            steam("Risk of Rain 2", 632360),
            steam("DREDGE", 1562430),
            steam("Hades II", 1145350),
            steam("Silent Hill 2", 2124490),
            steam("Ori and the Will of the Wisps", 1057090),
            steam("DAVE THE DIVER", 1868140),
            steam("It Takes Two", 1426210),
            steam("The Last of Us Part I", 1888930)
    );

    public BibliotecaService(BibliotecaDao bibliotecaDao, JuegoDao juegoDao) {
        this.bibliotecaDao = bibliotecaDao;
        this.juegoDao = juegoDao;
    }

    public List<BibliotecaItem> listar(Long idUsuario, String estado) {
        List<BibliotecaItem> items = bibliotecaDao.findByUsuario(idUsuario, estado);
        Map<String, String> portadasPorNombre = new LinkedHashMap<>();
        for (JuegoPopular popular : JUEGOS_POPULARES) {
            if (popular.urlPortada() != null) {
                portadasPorNombre.put(normalizar(popular.nombre()), popular.urlPortada());
            }
        }

        Map<Long, String> portadasActualizadas = new LinkedHashMap<>();
        for (BibliotecaItem item : items) {
            Juego juego = item.getJuego();
            if (juego.getUrlPortada() != null && !juego.getUrlPortada().isBlank()) continue;
            String portada = portadasPorNombre.get(normalizar(juego.getNombre()));
            if (portada == null) continue;
            juego.setUrlPortada(portada);
            portadasActualizadas.put(juego.getId(), portada);
        }
        juegoDao.updatePortadas(portadasActualizadas);
        return items;
    }

    public Optional<BibliotecaItem> buscarPorJuego(Long idUsuario, Long idJuego) {
        return bibliotecaDao.findByUsuarioAndJuego(idUsuario, idJuego);
    }

    @Transactional
    public Map<String, Integer> anadirPopulares(Long idUsuario) {
        List<BibliotecaItem> bibliotecaActual = bibliotecaDao.findByUsuario(idUsuario, null);
        Set<Long> idsEnBiblioteca = bibliotecaActual.stream()
                .map(BibliotecaItem::getIdJuego)
                .collect(java.util.stream.Collectors.toSet());

        List<String> nombres = JUEGOS_POPULARES.stream().map(JuegoPopular::nombre).toList();
        Map<String, Juego> juegosPorNombre = new LinkedHashMap<>();
        for (Juego juego : juegoDao.findByNombres(nombres)) {
            juegosPorNombre.putIfAbsent(normalizar(juego.getNombre()), juego);
        }

        List<Juego> juegosNuevos = new ArrayList<>();
        for (JuegoPopular popular : JUEGOS_POPULARES) {
            if (juegosPorNombre.containsKey(normalizar(popular.nombre()))) continue;
            Juego nuevo = new Juego();
            nuevo.setNombre(popular.nombre());
            nuevo.setUrlPortada(popular.urlPortada());
            nuevo.setUltimaActualizacion(Instant.now());
            juegosNuevos.add(nuevo);
        }
        for (Juego juego : juegoDao.insertAll(juegosNuevos)) {
            juegosPorNombre.putIfAbsent(normalizar(juego.getNombre()), juego);
        }

        Map<Long, String> portadasActualizadas = new LinkedHashMap<>();
        for (JuegoPopular popular : JUEGOS_POPULARES) {
            Juego juego = juegosPorNombre.get(normalizar(popular.nombre()));
            if (popular.urlPortada() != null && !popular.urlPortada().equals(juego.getUrlPortada())) {
                juego.setUrlPortada(popular.urlPortada());
                portadasActualizadas.put(juego.getId(), popular.urlPortada());
            }
        }
        juegoDao.updatePortadas(portadasActualizadas);

        List<BibliotecaItem> nuevasEntradas = new ArrayList<>();
        int yaExistentes = 0;
        for (JuegoPopular popular : JUEGOS_POPULARES) {
            Juego juego = juegosPorNombre.get(normalizar(popular.nombre()));
            if (!idsEnBiblioteca.add(juego.getId())) {
                yaExistentes++;
                continue;
            }

            BibliotecaItem item = new BibliotecaItem();
            item.setIdUsuario(idUsuario);
            item.setIdJuego(juego.getId());
            item.setEstado("pendiente");
            item.setHorasJugadas(0.0);
            nuevasEntradas.add(item);
        }

        bibliotecaDao.insertAll(nuevasEntradas);
        return Map.of("anadidos", nuevasEntradas.size(), "yaExistentes", yaExistentes);
    }

    private static String normalizar(String nombre) {
        return nombre.toLowerCase(Locale.ROOT);
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
