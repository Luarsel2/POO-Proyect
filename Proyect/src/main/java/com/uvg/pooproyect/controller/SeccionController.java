package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.HorarioRequest;
import com.uvg.pooproyect.dto.Requests.SeccionRequest;
import com.uvg.pooproyect.model.Seccion;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.service.SeccionService;

@RestController
@RequestMapping("/api/secciones")
public class SeccionController {
    private final SeccionService service;

    public SeccionController(SeccionService service) { this.service = service; }

    @PostMapping
    public Map<String, String> crearSeccion(@RequestBody SeccionRequest req) {
        return service.crearSeccion(req);
    }

    @PostMapping("/{idSeccion}/horarios")
    public Map<String, String> agregarHorario(@PathVariable("idSeccion") int idSeccion, @RequestBody HorarioRequest req) {
        return service.agregarHorario(idSeccion, req);
    }

    @PostMapping("/{idSeccion}/inscribir")
    public Map<String, String> agregarEstudiante(@PathVariable("idSeccion") int idSeccion) {
        return service.agregarEstudiante(idSeccion);
    }

    @PostMapping("/{idSeccion}/retirar")
    public Map<String, String> retirarEstudiante(@PathVariable("idSeccion") int idSeccion) {
        return service.retirarEstudiante(idSeccion);
    }

    @GetMapping
    public List<Seccion> obtenerTodas() {
        return service.obtenerTodas();
    }


    public Seccion buscarPorId(int idSeccion) {
        return service.buscarPorId(idSeccion);
    }
}
