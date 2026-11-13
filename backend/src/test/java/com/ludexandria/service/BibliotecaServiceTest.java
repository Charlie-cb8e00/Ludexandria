package com.ludexandria.service;

import com.ludexandria.dao.BibliotecaDao;
import com.ludexandria.dao.JuegoDao;
import com.ludexandria.model.BibliotecaItem;
import com.ludexandria.model.Juego;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BibliotecaServiceTest {

    @Test
    void anadirPopularesOmiteJuegosExistentesYUsaValoresPredeterminados() {
        BibliotecaDao bibliotecaDao = mock(BibliotecaDao.class);
        JuegoDao juegoDao = mock(JuegoDao.class);
        BibliotecaService service = new BibliotecaService(bibliotecaDao, juegoDao);

        BibliotecaItem existente = new BibliotecaItem();
        existente.setIdJuego(1L);
        when(bibliotecaDao.findByUsuario(7L, null)).thenReturn(List.of(existente));

        Juego minecraft = new Juego();
        minecraft.setId(1L);
        when(juegoDao.findByNombreExacto(anyString())).thenReturn(Optional.empty());
        when(juegoDao.findByNombreExacto(eq("Minecraft"))).thenReturn(Optional.of(minecraft));

        AtomicLong nextId = new AtomicLong(2);
        when(juegoDao.insert(any(Juego.class))).thenAnswer(invocation -> {
            Juego juego = invocation.getArgument(0);
            juego.setId(nextId.getAndIncrement());
            return juego;
        });

        var resultado = service.anadirPopulares(7L);

        assertEquals(9, resultado.get("anadidos"));
        assertEquals(1, resultado.get("yaExistentes"));
        ArgumentCaptor<Juego> juegosNuevos = ArgumentCaptor.forClass(Juego.class);
        verify(juegoDao, times(9)).insert(juegosNuevos.capture());
        assertFalse(juegosNuevos.getAllValues().stream().anyMatch(juego -> "Minecraft".equals(juego.getNombre())));

        ArgumentCaptor<BibliotecaItem> captor = ArgumentCaptor.forClass(BibliotecaItem.class);
        verify(bibliotecaDao, times(9)).insert(captor.capture());
        for (BibliotecaItem item : captor.getAllValues()) {
            assertEquals(7L, item.getIdUsuario());
            assertEquals("pendiente", item.getEstado());
            assertEquals(0.0, item.getHorasJugadas());
            assertNull(item.getNotaPersonal());
        }
    }
}
