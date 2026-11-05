package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.CarreraRequest;
import com.uvg.pooproyect.dto.Requests.PlanDeEstudioRequest;
import com.uvg.pooproyect.model.Carrera;
import com.uvg.pooproyect.model.Curso;
import com.uvg.pooproyect.model.PlanDeEstudio;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;

@Service
public class CarreraService {
    private final CarreraRepository repository;
    private final CursoRepository cursoRepository;

    public CarreraService(CarreraRepository repository, CursoRepository cursoRepository) {
        this.repository = repository;
        this.cursoRepository = cursoRepository;
    }

    public Map<String, String> crearCarrera(CarreraRequest req) {
        repository.agregar(new Carrera(req.nombre(), req.totalCreditos(), req.duracionPromedio()));
        repository.sincronizar();
        return Map.of("mensaje", "Carrera '" + req.nombre() + "' creada.");
    }

    public Map<String, String> agregarPlan(String nombreCarrera, PlanDeEstudioRequest req) {
        Carrera carrera = buscarPorNombre(nombreCarrera);
        if (carrera == null) {
            return Map.of("mensaje", "Error: carrera '" + nombreCarrera + "' no encontrada.");
        }
        carrera.agregarPlanDeEstudio(req.year(), req.estado());
        repository.sincronizar();
        return Map.of("mensaje", "Plan de estudio " + req.year() + " agregado a " + nombreCarrera);
    }

    public Map<String, String> agregarCursoAPlan(String nombreCarrera, int year, String nombreCurso) {
        Carrera carrera = buscarPorNombre(nombreCarrera);
        if (carrera == null) {
            return Map.of("mensaje", "Error: carrera '" + nombreCarrera + "' no encontrada.");
        }

        PlanDeEstudio plan = carrera.getPlanesDeEstudios().stream()
                .filter(p -> p.getYear() == year)
                .findFirst()
                .orElse(null);

        if (plan == null) {
            return Map.of("mensaje", "Error: no existe un plan del año " + year + " en " + nombreCarrera);
        }

        Curso curso = cursoRepository.buscarPorNombre(nombreCurso);
        if (curso == null) {
            return Map.of("mensaje", "Error: curso '" + nombreCurso + "' no encontrado.");
        }

        plan.agregarCurso(curso);
        repository.sincronizar();
        return Map.of("mensaje", "Curso '" + nombreCurso + "' agregado al plan " + year + " de " + nombreCarrera);
    }

    public List<Carrera> obtenerTodas() {
        return repository.obtenerTodos();
    }

    public Carrera buscarPorNombre(String nombre) {
        return repository.buscarPorNombre(nombre);
    }
}
