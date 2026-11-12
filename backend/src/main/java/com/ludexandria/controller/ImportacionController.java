package com.ludexandria.controller;

import com.ludexandria.service.ImportacionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/importaciones")
public class ImportacionController {

    private final ImportacionService importacionService;

    public ImportacionController(ImportacionService importacionService) {
        this.importacionService = importacionService;
    }

    /**
     * CSV esperado (cabecera opcional):
     * nombre,plataforma,estado,nota,horas
     * Ejemplo:
     * Hades,PC,pendiente,9,12.5
     */
    @PostMapping
    public Map<String, Object> importar(Authentication auth, @RequestParam("file") MultipartFile file) {
        Long uid = Long.valueOf(auth.getName());
        return importacionService.importarCsv(uid, file);
    }
}
