package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.PeriodoRequest;
import com.uvg.pooproyect.model.PeriodoAcademico;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;

@Service
public class PeriodoService {
    private final PeriodoRepository repository;

    public PeriodoService(PeriodoRepository repository) {
        this.repository = repository;
    }

    public Map<String, String> crearPeriodo(PeriodoRequest req) {
        PeriodoAcademico nuevo = new PeriodoAcademico(
                req.idPeriodo(), req.nombre(), req.fechaInicio(), req.fechaFin(),
                req.fechaInicioInscripcion(), req.fechaFinInscripcion(), req.estado(), null
        );
        repository.agregar(nuevo);
        repository.sincronizar();
        return Map.of("mensaje", "Periodo '" + req.nombre() + "' creado.");
    }

    public Map<String, String> abrirInscripciones(int idPeriodo) {
        PeriodoAcademico periodo = buscarPorId(idPeriodo);
        if (periodo == null) {
            return Map.of("mensaje", "Error: periodo " + idPeriodo + " no encontrado.");
        }
        periodo.abrirInscripciones();
        repository.sincronizar();
        return Map.of("mensaje", "Inscripciones abiertas para " + periodo.getNombre());
    }

    public Map<String, String> cerrarInscripciones(int idPeriodo) {
        PeriodoAcademico periodo = buscarPorId(idPeriodo);
        if (periodo == null) {
            return Map.of("mensaje", "Error: periodo " + idPeriodo + " no encontrado.");
        }
        periodo.cerrarInscripciones();
        repository.sincronizar();
        return Map.of("mensaje", "Inscripciones cerradas para " + periodo.getNombre());
    }

    public List<PeriodoAcademico> obtenerTodos() {
        return repository.obtenerTodos();
    }

    public PeriodoAcademico buscarPorId(int idPeriodo) {
        return repository.buscarPorId(idPeriodo);
    }
}
