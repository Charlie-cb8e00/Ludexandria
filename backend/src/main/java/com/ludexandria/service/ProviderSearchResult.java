package com.ludexandria.service;

import com.ludexandria.model.Juego;

import java.util.List;

public record ProviderSearchResult(List<Juego> juegos, String advertencia) {
    public static ProviderSearchResult sinResultados() {
        return new ProviderSearchResult(List.of(), null);
    }
}
