package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.HorarioRequest;
import com.uvg.pooproyect.dto.Requests.SeccionRequest;
import com.uvg.pooproyect.model.Horario;
import com.uvg.pooproyect.model.Profesor;
import com.uvg.pooproyect.model.Seccion;
import java.util.List;
import java.util.Map;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;

@Service
public class SeccionService {
    private final SeccionRepository repository;
    private final ProfesorRepository profesorRepository;

    public SeccionService(SeccionRepository repository, ProfesorRepository profesorRepository) {
        this.repository = repository;
        this.profesorRepository = profesorRepository;
    }

    public Map<String, String> crearSeccion(SeccionRequest req) {
        Profesor profesor = profesorRepository.buscarPorCodigo(req.codigoProfesor());
        Seccion nueva = new Seccion(req.idSeccion(), req.codigo(), req.cupoMaximo(), 0, "ACTIVA", profesor, null);
        repository.agregar(nueva);
        repository.sincronizar();
        String msgProfesor = profesor != null
                ? " con profesor " + profesor.getNombre() + " " + profesor.getApellido()
                : " (sin profesor asignado, verifica el código)";
        return Map.of("mensaje", "Sección " + req.codigo() + " creada" + msgProfesor);
    }

    public Map<String, String> agregarHorario(int idSeccion, HorarioRequest req) {
        Seccion seccion = buscarPorId(idSeccion);
        if (seccion == null) {
            return Map.of("mensaje", "Error: sección " + idSeccion + " no encontrada.");
        }
        seccion.agregarHorario(req.idHorario(), req.diaSemana(), req.horaInicio(), req.horaFin());
        repository.sincronizar();
        return Map.of("mensaje", "Horario agregado a la sección " + seccion.getCodigo());
    }

    public Map<String, String> agregarEstudiante(int idSeccion) {
        Seccion seccion = buscarPorId(idSeccion);
        if (seccion == null) {
            return Map.of("mensaje", "Error: sección " + idSeccion + " no encontrada.");
        }
        boolean habiaCupo = seccion.verificarCupos();
        seccion.agregarEstudiante();
        repository.sincronizar();
        return Map.of("mensaje", habiaCupo
                ? "Estudiante agregado. Cupos disponibles: " + seccion.obtenerCuposDisponibles()
                : "Sin cupos disponibles en esta sección.");
    }

    public Map<String, String> retirarEstudiante(int idSeccion) {
        Seccion seccion = buscarPorId(idSeccion);
        if (seccion == null) {
            return Map.of("mensaje", "Error: sección " + idSeccion + " no encontrada.");
        }
        seccion.retirarEstudiante();
        repository.sincronizar();
        return Map.of("mensaje", "Estudiante retirado. Cupos disponibles: " + seccion.obtenerCuposDisponibles());
    }

    public List<Seccion> obtenerTodas() {
        return repository.obtenerTodos();
    }

    public Seccion buscarPorId(int idSeccion) {
        return repository.buscarPorId(idSeccion);
    }
}
