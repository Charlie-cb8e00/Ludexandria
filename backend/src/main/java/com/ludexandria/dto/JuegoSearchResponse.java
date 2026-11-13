package com.ludexandria.dto;

import com.ludexandria.model.Juego;

import java.util.List;

public record JuegoSearchResponse(List<Juego> juegos, List<String> advertencias) {
}
