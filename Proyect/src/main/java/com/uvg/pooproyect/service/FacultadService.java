package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.DepartamentoRequest;
import com.uvg.pooproyect.dto.Requests.FacultadRequest;
import com.uvg.pooproyect.model.Carrera;
import com.uvg.pooproyect.model.Departamento;
import com.uvg.pooproyect.model.Facultad;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;

@Service
public class FacultadService {
    private final FacultadRepository repository;
    private final CarreraRepository carreraRepository;

    public FacultadService(FacultadRepository repository, CarreraRepository carreraRepository) {
        this.repository = repository;
        this.carreraRepository = carreraRepository;
    }

    public Map<String, String> crearFacultad(FacultadRequest req) {
        repository.agregar(new Facultad(req.nombre()));
        repository.sincronizar();
        return Map.of("mensaje", "Facultad '" + req.nombre() + "' creada.");
    }

    public Map<String, String> agregarDepartamento(String nombreFacultad, DepartamentoRequest req) {
        Facultad facultad = buscarPorNombre(nombreFacultad);
        if (facultad == null) {
            return Map.of("mensaje", "Error: facultad '" + nombreFacultad + "' no encontrada.");
        }
        facultad.agregarDepartamento(req.nombreDepartamento());
        repository.sincronizar();
        return Map.of("mensaje", "Departamento '" + req.nombreDepartamento() + "' agregado a " + nombreFacultad);
    }

    public Map<String, String> asociarCarrera(String nombreFacultad, String nombreCarrera) {
        Facultad facultad = buscarPorNombre(nombreFacultad);
        Carrera carrera = carreraRepository.buscarPorNombre(nombreCarrera);
        if (facultad == null) {
            return Map.of("mensaje", "Error: facultad '" + nombreFacultad + "' no encontrada.");
        }
        if (carrera == null) {
            return Map.of("mensaje", "Error: carrera '" + nombreCarrera + "' no encontrada.");
        }
        facultad.agregarCarrera(carrera);
        repository.sincronizar();
        return Map.of("mensaje", "Carrera '" + nombreCarrera + "' asociada a la facultad " + nombreFacultad);
    }

    public List<Facultad> obtenerTodas() {
        return repository.obtenerTodos();
    }

    public Facultad buscarPorNombre(String nombre) {
        return repository.buscarPorNombre(nombre);
    }
}
