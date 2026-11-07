package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.EstudianteRequest;
import com.uvg.pooproyect.dto.RespuestaAcademica.*;
import com.uvg.pooproyect.model.*;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AsignacionService {
    private final EstudianteRepository estudianteRepository;
    private final AsignacionRepository asignacionRepository;
    private final CatalogoSeccionRepository catalogoRepository;

    public AsignacionService(EstudianteRepository estudianteRepository, AsignacionRepository asignacionRepository, CatalogoSeccionRepository catalogoRepository) {
        this.estudianteRepository = estudianteRepository;
        this.asignacionRepository = asignacionRepository;
        this.catalogoRepository = catalogoRepository;
    }

    public Map<String, Object> asignarCurso(int carnetFinal, Map<String, Object> req) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return Map.of("ok", false, "mensaje", "Estudiante no encontrado.");
        }

        Object idObj = req.get("idSeccion");
        if (idObj == null) {
            return Map.of("ok", false, "mensaje", "Debe especificar idSeccion.");
        }
        int idSeccion;
        if (idObj instanceof Number n) {
            idSeccion = n.intValue();
        } else {
            try {
                idSeccion = Integer.parseInt(idObj.toString());
            } catch (Exception ex) {
                return Map.of("ok", false, "mensaje", "Identificador de sección inválido.");
            }
        }

        SeccionCatalogo nueva = catalogoRepository.buscarSeccionCatalogo(idSeccion);
        if (nueva == null) {
            return Map.of("ok", false, "mensaje", "Sección no encontrada.");
        }

        List<Integer> asignados = new ArrayList<>(asignacionRepository.obtenerAsignacionesPorCarnet(carnetFinal));
        if (asignados.contains(idSeccion)) {
            return Map.of("ok", false, "mensaje", "Ya estás inscrito en esta sección.");
        }

        for (Integer yaId : new ArrayList<>(asignados)) {
            SeccionCatalogo yaSec = catalogoRepository.buscarSeccionCatalogo(yaId);
            if (yaSec != null && yaSec.codigoCurso.equalsIgnoreCase(nueva.codigoCurso)) {
                asignados.remove(yaId);
                if (yaSec.getCupoActual() > 0) {
                    yaSec.liberarCupo();
                }
            }
        }

        if (nueva.getCupoActual() >= nueva.cupoMaximo) {
            return Map.of("ok", false, "mensaje", "No hay cupos disponibles en esta sección.");
        }

        asignados.add(idSeccion);
        nueva.reservarCupo();
        asignacionRepository.guardarAsignaciones(carnetFinal, asignados);
        est.inscribirCurso();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("mensaje", "Asignación exitosa: " + nueva.nombreCurso + " (Sección " + nueva.codigoSeccion + " - " + nueva.jornada + ")");
        return res;
    }

    public Map<String, Object> asignarHorarioCompleto(int carnetFinal, Map<String, Object> req) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return Map.of("ok", false, "mensaje", "Estudiante no encontrado.");
        }

        String jornada = String.valueOf(req.getOrDefault("jornada", "Matutina")).trim();
        boolean esNocturna = jornada.equalsIgnoreCase("Nocturna");

        List<Integer> idsDeseados = esNocturna
                ? List.of(201, 202, 203, 204, 205, 206)
                : List.of(101, 102, 103, 104, 105, 106);

        List<Integer> asignados = new ArrayList<>(asignacionRepository.obtenerAsignacionesPorCarnet(carnetFinal));
        for (Integer yaId : new ArrayList<>(asignados)) {
            SeccionCatalogo sec = catalogoRepository.buscarSeccionCatalogo(yaId);
            if (sec != null && sec.getCupoActual() > 0) {
                sec.liberarCupo();
            }
        }
        asignados.clear();

        for (Integer nuevoId : idsDeseados) {
            SeccionCatalogo sec = catalogoRepository.buscarSeccionCatalogo(nuevoId);
            if (sec != null) {
                asignados.add(nuevoId);
                sec.reservarCupo();
            }
        }

        asignacionRepository.guardarAsignaciones(carnetFinal, asignados);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("jornada", esNocturna ? "Nocturna" : "Matutina");
        res.put("mensaje", "Horario " + (esNocturna ? "Nocturno (15:00 - 21:00)" : "Matutino (07:00 - 12:00 / Vie 13:00)") + " asignado exitosamente (6 asignaturas).");
        return res;
    }

    public Map<String, Object> retirarTodosLosCursos(int carnetFinal) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return Map.of("ok", false, "mensaje", "Estudiante no encontrado.");
        }

        List<Integer> asignados = new ArrayList<>(asignacionRepository.obtenerAsignacionesPorCarnet(carnetFinal));
        if (asignados != null) {
            for (Integer idSec : asignados) {
                SeccionCatalogo sec = catalogoRepository.buscarSeccionCatalogo(idSec);
                if (sec != null && sec.getCupoActual() > 0) {
                    sec.liberarCupo();
                }
            }
            asignados.clear();
        }
        asignacionRepository.guardarAsignaciones(carnetFinal, asignados);

        return Map.of("ok", true, "mensaje", "Se han retirado todas las asignaturas correctamente.");
    }

    public Map<String, Object> retirarCurso(int carnetFinal, Map<String, Object> req) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return Map.of("ok", false, "mensaje", "Estudiante no encontrado.");
        }

        Object idObj = req.get("idSeccion");
        if (idObj == null) {
            return Map.of("ok", false, "mensaje", "Debe especificar idSeccion.");
        }
        int idSeccion;
        if (idObj instanceof Number n) {
            idSeccion = n.intValue();
        } else {
            try {
                idSeccion = Integer.parseInt(idObj.toString());
            } catch (Exception ex) {
                return Map.of("ok", false, "mensaje", "Identificador de sección inválido.");
            }
        }

        List<Integer> asignados = new ArrayList<>(asignacionRepository.obtenerAsignacionesPorCarnet(carnetFinal));
        if (asignados == null || !asignados.contains(idSeccion)) {
            return Map.of("ok", false, "mensaje", "El estudiante no tiene asignada esa sección.");
        }

        asignados.remove(Integer.valueOf(idSeccion));
        SeccionCatalogo sec = catalogoRepository.buscarSeccionCatalogo(idSeccion);
        if (sec != null && sec.getCupoActual() > 0) {
            sec.liberarCupo();
        }
        asignacionRepository.guardarAsignaciones(carnetFinal, asignados);
        est.retirarCurso();

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("ok", true);
        res.put("mensaje", "Curso retirado exitosamente: " + (sec != null ? sec.nombreCurso : "Sección " + idSeccion));
        return res;
    }
}
