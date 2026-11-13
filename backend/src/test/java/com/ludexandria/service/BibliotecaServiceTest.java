package com.ludexandria.service;

import com.ludexandria.dao.BibliotecaDao;
import com.ludexandria.dao.JuegoDao;
import com.ludexandria.dto.BibliotecaRequest;
import com.ludexandria.model.BibliotecaItem;
import com.ludexandria.model.Juego;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class BibliotecaServiceTest {

    @Test
    void actualizarPermiteBorrarLaNotaPersonal() {
        BibliotecaDao bibliotecaDao = mock(BibliotecaDao.class);
        JuegoDao juegoDao = mock(JuegoDao.class);
        BibliotecaService service = new BibliotecaService(bibliotecaDao, juegoDao);

        BibliotecaItem item = new BibliotecaItem();
        item.setId(3L);
        item.setIdUsuario(7L);
        item.setNotaPersonal(8.0);
        when(bibliotecaDao.findByIdAndUsuario(3L, 7L)).thenReturn(Optional.of(item));
        BibliotecaRequest request = new BibliotecaRequest();
        request.setNotaPersonal(null);

        BibliotecaItem resultado = service.actualizar(7L, 3L, request);

        assertNull(resultado.getNotaPersonal());
    }

    @Test
    void listarRellenaPortadaPopularAusente() {
        BibliotecaDao bibliotecaDao = mock(BibliotecaDao.class);
        JuegoDao juegoDao = mock(JuegoDao.class);
        BibliotecaService service = new BibliotecaService(bibliotecaDao, juegoDao);

        Juego juego = new Juego();
        juego.setId(1L);
        juego.setNombre("Stardew Valley");
        BibliotecaItem item = new BibliotecaItem();
        item.setJuego(juego);
        when(bibliotecaDao.findByUsuario(7L, null)).thenReturn(List.of(item));

        List<BibliotecaItem> resultado = service.listar(7L, null);

        String portada = "https://cdn.akamai.steamstatic.com/steam/apps/413150/library_600x900.jpg";
        assertEquals(portada, resultado.get(0).getJuego().getUrlPortada());
        verify(juegoDao).updatePortadas(Map.of(1L, portada));
    }

    @Test
    void anadirPopularesOmiteJuegosExistentesYUsaValoresPredeterminados() {
        BibliotecaDao bibliotecaDao = mock(BibliotecaDao.class);
        JuegoDao juegoDao = mock(JuegoDao.class);
        BibliotecaService service = new BibliotecaService(bibliotecaDao, juegoDao);

        BibliotecaItem existente = new BibliotecaItem();
        existente.setIdJuego(1L);
        when(bibliotecaDao.findByUsuario(7L, null)).thenReturn(List.of(existente));

        Juego stardewValley = new Juego();
        stardewValley.setId(1L);
        stardewValley.setNombre("Stardew Valley");
        when(juegoDao.findByNombres(anyList())).thenReturn(List.of(stardewValley));
        when(juegoDao.insertAll(anyList())).thenAnswer(invocation -> {
            List<Juego> juegos = invocation.getArgument(0);
            long nextId = 2;
            for (Juego juego : juegos) {
                juego.setId(nextId++);
            }
            return juegos;
        });

        var resultado = service.anadirPopulares(7L);

        assertEquals(49, resultado.get("anadidos"));
        assertEquals(1, resultado.get("yaExistentes"));
        verify(juegoDao).insertAll(argThat(juegos ->
                juegos.size() == 49
                        && juegos.stream().noneMatch(juego -> "Stardew Valley".equals(juego.getNombre()))
                        && juegos.stream().filter(juego -> juego.getUrlPortada() != null).count() == 47));
        verify(juegoDao).updatePortadas(argThat(portadas ->
                portadas.size() == 1
                        && "https://cdn.akamai.steamstatic.com/steam/apps/413150/library_600x900.jpg"
                        .equals(portadas.get(1L))));
        verify(bibliotecaDao).insertAll(argThat(items ->
                items.size() == 49 && items.stream().allMatch(item ->
                        item.getIdUsuario().equals(7L)
                                && "pendiente".equals(item.getEstado())
                                && item.getHorasJugadas().equals(0.0)
                                && item.getNotaPersonal() == null)));
    }
}
