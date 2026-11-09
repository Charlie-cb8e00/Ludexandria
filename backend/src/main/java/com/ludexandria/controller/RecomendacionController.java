package com.ludexandria.controller;

import com.ludexandria.dto.RecomendacionRequest;
import com.ludexandria.service.RecomendacionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recomendaciones")
public class RecomendacionController {
    private final RecomendacionService recomendacionService;

    public RecomendacionController(RecomendacionService recomendacionService) {
        this.recomendacionService = recomendacionService;
    }

    @PostMapping
    public List<Map<String, Object>> recomendar(Authentication auth, @RequestBody RecomendacionRequest req) {
        Long uid = Long.valueOf(auth.getName());
        return recomendacionService.recomendar(uid, req);
    }
}
