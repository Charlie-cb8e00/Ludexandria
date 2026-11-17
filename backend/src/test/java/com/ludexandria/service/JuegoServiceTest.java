package com.ludexandria.service;

import com.ludexandria.dao.JuegoDao;
import com.ludexandria.model.Juego;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

class JuegoServiceTest {

    @Test
    void buscarConsultaFuentesYDevuelveLaPaginaDeLaCache() {
        JuegoDao juegoDao = mock(JuegoDao.class);
        IgdbService igdbService = mock(IgdbService.class);
        SteamService steamService = mock(SteamService.class);
        JuegoService service = new JuegoService(juegoDao, igdbService, steamService);

        Juego igdb = juego(1L, "Hades");
        Juego steam = juego(2L, "Hades II");
        when(igdbService.buscar("Hades", 20)).thenReturn(new ProviderSearchResult(List.of(igdb), null));
        when(steamService.buscar("Hades", 20)).thenReturn(new ProviderSearchResult(List.of(steam), null));
        when(juegoDao.search("Hades", 20, 0)).thenReturn(List.of(igdb, steam));
        when(juegoDao.countSearch("Hades")).thenReturn(2L);

        assertEquals(List.of(igdb, steam), service.buscar("Hades", 0, 20));
        verify(igdbService).buscar("Hades", 20);
        verify(steamService).buscar("Hades", 20);
        verify(juegoDao).search("Hades", 20, 0);
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
        when(juegoDao.search("Hades", 20, 0)).thenReturn(List.of());
        when(juegoDao.countSearch("Hades")).thenReturn(0L);

        var response = service.buscarCatalogo("Hades", 0, 20);

        assertEquals(List.of(), response.juegos());
        assertEquals(List.of("No se pudo conectar con Steam."), response.advertencias());
        assertEquals(0, response.page());
        assertEquals(0, response.totalElements());
    }

    @Test
    void lasPaginasSiguientesConsultanLaCacheSinVolverALlamarALosProveedores() {
        JuegoDao juegoDao = mock(JuegoDao.class);
        IgdbService igdbService = mock(IgdbService.class);
        SteamService steamService = mock(SteamService.class);
        JuegoService service = new JuegoService(juegoDao, igdbService, steamService);
        Juego juego = juego(21L, "Hades II");
        when(juegoDao.search("Hades", 20, 20)).thenReturn(List.of(juego));
        when(juegoDao.countSearch("Hades")).thenReturn(21L);

        var response = service.buscarCatalogo("Hades", 1, 20);

        assertEquals(List.of(juego), response.juegos());
        assertEquals(1, response.page());
        assertEquals(2, response.totalPages());
        assertFalse(response.hasNext());
        verifyNoInteractions(igdbService, steamService);
    }

    private Juego juego(Long id, String nombre) {
        Juego juego = new Juego();
        juego.setId(id);
        juego.setNombre(nombre);
        return juego;
    }
}
