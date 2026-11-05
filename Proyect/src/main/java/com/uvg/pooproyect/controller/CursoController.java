package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.CursoRequest;
import com.uvg.pooproyect.model.Curso;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.service.CursoService;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {
    private final CursoService service;

    public CursoController(CursoService service) { this.service = service; }

    @PostMapping
    public Map<String, String> crearCurso(@RequestBody CursoRequest req) {
        return service.crearCurso(req);
    }

    @GetMapping
    public List<Curso> obtenerTodos() {
        return service.obtenerTodos();
    }


    public Curso buscarPorNombre(String nombre) {
        return service.buscarPorNombre(nombre);
    }
}
