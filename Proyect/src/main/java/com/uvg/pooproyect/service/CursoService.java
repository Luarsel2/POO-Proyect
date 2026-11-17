package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.CursoRequest;
import com.uvg.pooproyect.model.Curso;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;

@Service
public class CursoService {
    private final CursoRepository repository;

    public CursoService(CursoRepository repository) {
        this.repository = repository;
    }

    public Map<String, String> crearCurso(CursoRequest req) {
        Curso nuevo = new Curso(req.nombre(), req.creditos(), null);
        repository.agregar(nuevo);
        repository.sincronizar();
        return Map.of("mensaje", "Curso '" + req.nombre() + "' creado con " + req.creditos() + " créditos.");
    }

    public List<Curso> obtenerTodos() {
        return repository.obtenerTodos();
    }

    public Curso buscarPorNombre(String nombre) {
        return repository.buscarPorNombre(nombre);
    }
}
