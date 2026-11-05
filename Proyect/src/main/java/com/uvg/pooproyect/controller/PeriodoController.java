package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.PeriodoRequest;
import com.uvg.pooproyect.model.PeriodoAcademico;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.service.PeriodoService;

@RestController
@RequestMapping("/api/periodos")
public class PeriodoController {
    private final PeriodoService service;

    public PeriodoController(PeriodoService service) { this.service = service; }

    @PostMapping
    public Map<String, String> crearPeriodo(@RequestBody PeriodoRequest req) {
        return service.crearPeriodo(req);
    }

    @PostMapping("/{idPeriodo}/abrir")
    public Map<String, String> abrirInscripciones(@PathVariable("idPeriodo") int idPeriodo) {
        return service.abrirInscripciones(idPeriodo);
    }

    @PostMapping("/{idPeriodo}/cerrar")
    public Map<String, String> cerrarInscripciones(@PathVariable("idPeriodo") int idPeriodo) {
        return service.cerrarInscripciones(idPeriodo);
    }

    @GetMapping
    public List<PeriodoAcademico> obtenerTodos() {
        return service.obtenerTodos();
    }


    public PeriodoAcademico buscarPorId(int idPeriodo) {
        return service.buscarPorId(idPeriodo);
    }
}
