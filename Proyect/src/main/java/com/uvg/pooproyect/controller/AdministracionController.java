package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.AdministracionRequest;
import com.uvg.pooproyect.model.Administracion;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.service.AdministracionService;

@RestController
@RequestMapping("/api/administracion")
public class AdministracionController {
    private final AdministracionService service;

    public AdministracionController(AdministracionService service) { this.service = service; }

    @PostMapping
    public Map<String, String> registrar(@RequestBody AdministracionRequest req) {
        return service.registrar(req);
    }

    @GetMapping
    public List<Administracion> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{codigo}")
    public Administracion buscarPorCodigo(@PathVariable("codigo") int codigo) {
        return service.buscarPorCodigo(codigo);
    }
}
