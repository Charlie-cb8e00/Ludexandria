package com.ludexandria.dto;

import com.ludexandria.model.Juego;

import java.util.List;

public record JuegoSearchResponse(
        List<Juego> juegos,
        List<String> advertencias,
        int page,
        int size,
        long totalElements,
        long totalPages,
        boolean hasNext) {
    public JuegoSearchResponse(List<Juego> juegos, List<String> advertencias, int page, int size, long totalElements) {
        this(juegos, advertencias, page, size, totalElements,
                totalElements == 0 ? 0 : (totalElements - 1) / size + 1,
                page + 1L < (totalElements == 0 ? 0 : (totalElements - 1) / size + 1));
    }
}
