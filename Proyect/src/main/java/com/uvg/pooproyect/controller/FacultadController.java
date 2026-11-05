package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.DepartamentoRequest;
import com.uvg.pooproyect.dto.Requests.FacultadRequest;
import com.uvg.pooproyect.model.Facultad;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.service.FacultadService;

@RestController
@RequestMapping("/api/facultades")
public class FacultadController {
    private final FacultadService service;

    public FacultadController(FacultadService service) { this.service = service; }

    @PostMapping
    public Map<String, String> crearFacultad(@RequestBody FacultadRequest req) {
        return service.crearFacultad(req);
    }

    @PostMapping("/{nombreFacultad}/departamentos")
    public Map<String, String> agregarDepartamento(@PathVariable("nombreFacultad") String nombreFacultad, @RequestBody DepartamentoRequest req) {
        return service.agregarDepartamento(nombreFacultad, req);
    }

    @PostMapping("/{nombreFacultad}/carreras/{nombreCarrera}")
    public Map<String, String> asociarCarrera(@PathVariable("nombreFacultad") String nombreFacultad, @PathVariable("nombreCarrera") String nombreCarrera) {
        return service.asociarCarrera(nombreFacultad, nombreCarrera);
    }

    @GetMapping
    public List<Facultad> obtenerTodas() {
        return service.obtenerTodas();
    }


    public Facultad buscarPorNombre(String nombre) {
        return service.buscarPorNombre(nombre);
    }
}
