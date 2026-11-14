package com.uvg.pooproyect.controller;

import java.io.UncheckedIOException;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> datosInvalidos(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", error.getMessage()));
    }
    @ExceptionHandler(UncheckedIOException.class)
    public ResponseEntity<Map<String, Object>> persistenciaNoDisponible(UncheckedIOException error) {
        return ResponseEntity.internalServerError().body(Map.of("ok", false,
            "mensaje", "No se pudieron guardar o consultar los datos. Revisa el acceso a la carpeta de datos."));
    }
}
