package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.DetalleInscripcionRequest;
import com.uvg.pooproyect.dto.Requests.InscripcionRequest;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.model.Inscripcion;
import com.uvg.pooproyect.model.PeriodoAcademico;
import com.uvg.pooproyect.model.Seccion;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;

@Service
public class InscripcionService {
    private final InscripcionRepository repository;
    private final EstudianteRepository estudianteRepository;
    private final PeriodoRepository periodoRepository;
    private final SeccionRepository seccionRepository;

    public InscripcionService(InscripcionRepository repository, EstudianteRepository estudianteRepository, PeriodoRepository periodoRepository, SeccionRepository seccionRepository) {
        this.repository = repository;
        this.estudianteRepository = estudianteRepository;
        this.periodoRepository = periodoRepository;
        this.seccionRepository = seccionRepository;
    }

    public Map<String, String> procesarInscripcion(InscripcionRequest req) {
        Estudiante estudiante = estudianteRepository.buscarPorCarnet(req.carnetEstudiante());
        PeriodoAcademico periodo = periodoRepository.buscarPorId(req.idPeriodo());

        if (estudiante == null) {
            return Map.of("mensaje", "Error: estudiante con carnet " + req.carnetEstudiante() + " no encontrado.");
        }
        if (periodo == null) {
            return Map.of("mensaje", "Error: periodo con id " + req.idPeriodo() + " no encontrado.");
        }

        Inscripcion inscripcion = new Inscripcion(
                req.id(), req.fechaDeInscripcion(), req.estado(), req.totalCreditos(), estudiante, periodo
        );
        repository.agregar(inscripcion);
        repository.sincronizar();

        return Map.of("mensaje", "Inscripción completada para " + estudiante.getNombre()
                + " en el periodo " + periodo.getNombre());
    }

    public Map<String, String> agregarDetalle(int idInscripcion, DetalleInscripcionRequest req) {
        Inscripcion inscripcion = buscarPorId(idInscripcion);
        if (inscripcion == null) {
            return Map.of("mensaje", "Error: inscripción " + idInscripcion + " no encontrada.");
        }
        Seccion seccion = seccionRepository.buscarPorId(req.idSeccion());
        if (seccion == null) {
            return Map.of("mensaje", "Error: sección " + req.idSeccion() + " no encontrada.");
        }
        inscripcion.agregarDetalle(req.idDetalle(), req.fechaInscripcion(), req.estado(), seccion);
        repository.sincronizar();
        return Map.of("mensaje", "Sección " + seccion.getCodigo() + " agregada a la inscripción " + idInscripcion);
    }

    public List<Inscripcion> obtenerTodas() {
        return repository.obtenerTodos();
    }

    public Inscripcion buscarPorId(int id) {
        return repository.buscarPorId(id);
    }
}
