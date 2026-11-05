package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.DetalleInscripcionRequest;
import com.uvg.pooproyect.dto.Requests.InscripcionRequest;
import com.uvg.pooproyect.model.Inscripcion;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.service.InscripcionService;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {
    private final InscripcionService service;

    public InscripcionController(InscripcionService service) { this.service = service; }

    @PostMapping
    public Map<String, String> procesarInscripcion(@RequestBody InscripcionRequest req) {
        return service.procesarInscripcion(req);
    }

    @PostMapping("/{idInscripcion}/detalles")
    public Map<String, String> agregarDetalle(@PathVariable("idInscripcion") int idInscripcion, @RequestBody DetalleInscripcionRequest req) {
        return service.agregarDetalle(idInscripcion, req);
    }

    @GetMapping
    public List<Inscripcion> obtenerTodas() {
        return service.obtenerTodas();
    }


    public Inscripcion buscarPorId(int id) {
        return service.buscarPorId(id);
    }
}
