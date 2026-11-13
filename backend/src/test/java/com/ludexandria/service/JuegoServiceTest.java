package com.ludexandria.service;

import com.ludexandria.dao.JuegoDao;
import com.ludexandria.model.Juego;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JuegoServiceTest {

    @Test
    void buscarCombinaFuentesYEliminaJuegosDuplicados() {
        JuegoDao juegoDao = mock(JuegoDao.class);
        IgdbService igdbService = mock(IgdbService.class);
        SteamService steamService = mock(SteamService.class);
        JuegoService service = new JuegoService(juegoDao, igdbService, steamService);

        Juego igdb = juego(1L, "Hades");
        Juego steam = juego(2L, "Hades II");
        Juego localDuplicado = juego(1L, "Hades");
        when(igdbService.buscar("Hades", 20)).thenReturn(new ProviderSearchResult(List.of(igdb), null));
        when(steamService.buscar("Hades", 20)).thenReturn(new ProviderSearchResult(List.of(steam), null));
        when(juegoDao.search("Hades", 50, 0)).thenReturn(List.of(localDuplicado));

        assertEquals(List.of(igdb, steam), service.buscar("Hades", 0, 20));
        verify(igdbService).buscar("Hades", 20);
        verify(steamService).buscar("Hades", 20);
    }

    @Test
    void buscarCatalogoDevuelveAdvertenciasDeFuentesNoDisponibles() {
        JuegoDao juegoDao = mock(JuegoDao.class);
        IgdbService igdbService = mock(IgdbService.class);
        SteamService steamService = mock(SteamService.class);
        JuegoService service = new JuegoService(juegoDao, igdbService, steamService);
        when(igdbService.buscar("Hades", 20)).thenReturn(ProviderSearchResult.sinResultados());
        when(steamService.buscar("Hades", 20)).thenReturn(
                new ProviderSearchResult(List.of(), "No se pudo conectar con Steam."));
        when(juegoDao.search("Hades", 50, 0)).thenReturn(List.of());

        var response = service.buscarCatalogo("Hades", 0, 20);

        assertEquals(List.of(), response.juegos());
        assertEquals(List.of("No se pudo conectar con Steam."), response.advertencias());
    }

    private Juego juego(Long id, String nombre) {
        Juego juego = new Juego();
        juego.setId(id);
        juego.setNombre(nombre);
        return juego;
    }
}
