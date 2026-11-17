package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.ProfesorRequest;
import com.uvg.pooproyect.model.Profesor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.service.ProfesorService;

@RestController
@RequestMapping("/api/profesores")
public class ProfesorController {
    private final ProfesorService service;

    public ProfesorController(ProfesorService service) { this.service = service; }

    @PostMapping
    public Map<String, String> registrarProfesor(@RequestBody ProfesorRequest req) {
        return service.registrarProfesor(req);
    }

    @GetMapping
    public List<Profesor> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{codigo}")
    public Profesor buscarPorCodigo(@PathVariable("codigo") int codigo) {
        return service.buscarPorCodigo(codigo);
    }
}
