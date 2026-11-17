package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.CarreraRequest;
import com.uvg.pooproyect.dto.Requests.PlanDeEstudioRequest;
import com.uvg.pooproyect.model.Carrera;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.service.CarreraService;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {
    private final CarreraService service;

    public CarreraController(CarreraService service) { this.service = service; }

    @PostMapping
    public Map<String, String> crearCarrera(@RequestBody CarreraRequest req) {
        return service.crearCarrera(req);
    }

    @PostMapping("/{nombreCarrera}/planes")
    public Map<String, String> agregarPlan(@PathVariable("nombreCarrera") String nombreCarrera, @RequestBody PlanDeEstudioRequest req) {
        return service.agregarPlan(nombreCarrera, req);
    }

    @PostMapping("/{nombreCarrera}/planes/{year}/cursos/{nombreCurso}")
    public Map<String, String> agregarCursoAPlan(@PathVariable("nombreCarrera") String nombreCarrera, @PathVariable("year") int year, @PathVariable("nombreCurso") String nombreCurso) {
        return service.agregarCursoAPlan(nombreCarrera, year, nombreCurso);
    }

    @GetMapping
    public List<Carrera> obtenerTodas() {
        return service.obtenerTodas();
    }


    public Carrera buscarPorNombre(String nombre) {
        return service.buscarPorNombre(nombre);
    }
}
