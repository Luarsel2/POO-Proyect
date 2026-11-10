package com.uvg.pooproyect.service;

import com.uvg.pooproyect.model.SeccionCatalogo;
import com.uvg.pooproyect.repository.*;
import com.uvg.pooproyect.service.reglas.ReglaAsignacion;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AsignacionService {
    private final EstudianteRepository estudiantes;
    private final AsignacionRepository asignaciones;
    private final CatalogoSeccionRepository catalogo;
    private final List<ReglaAsignacion> reglas;

    public AsignacionService(EstudianteRepository estudiantes, AsignacionRepository asignaciones,
                             CatalogoSeccionRepository catalogo, List<ReglaAsignacion> reglas) {
        this.estudiantes = estudiantes;
        this.asignaciones = asignaciones;
        this.catalogo = catalogo;
        this.reglas = List.copyOf(reglas);
    }

    public synchronized Map<String, Object> asignarCurso(int carnet, Map<String, Object> req) {
        if (estudiantes.buscarPorCarnet(carnet) == null) return error("Estudiante no encontrado.");
        Integer id = idSeccion(req);
        if (id == null) return error("Debe especificar un identificador de sección válido.");
        SeccionCatalogo nueva = catalogo.buscarSeccionCatalogo(id);
        if (nueva == null) return error("Sección no encontrada.");
        List<Integer> anteriores = asignaciones.obtenerAsignacionesPorCarnet(carnet);
        if (anteriores.contains(id)) return error("Ya estás inscrito en esta sección.");
        List<SeccionCatalogo> propuestas = new ArrayList<>(secciones(anteriores));
        propuestas.removeIf(s -> s.codigoCurso.equalsIgnoreCase(nueva.codigoCurso));
        propuestas.add(nueva);
        Optional<String> problema = guardarSeleccion(carnet, anteriores, propuestas);
        if (problema.isPresent()) return error(problema.get());
        return Map.of("ok", true, "mensaje", "Asignación exitosa: " + nueva.nombreCurso
            + " (Sección " + nueva.codigoSeccion + " - " + nueva.jornada + ")");
    }

    public synchronized Map<String, Object> asignarHorarioCompleto(int carnet, Map<String, Object> req) {
        if (estudiantes.buscarPorCarnet(carnet) == null) return error("Estudiante no encontrado.");
        String jornada = String.valueOf(req.getOrDefault("jornada", "Matutina")).trim();
        List<SeccionCatalogo> propuestas = catalogo.obtenerCatalogoSecciones().stream()
            .filter(s -> s.jornada.equalsIgnoreCase(jornada)).toList();
        if (propuestas.isEmpty()) return error("No hay secciones para esa jornada.");
        List<Integer> anteriores = asignaciones.obtenerAsignacionesPorCarnet(carnet);
        Optional<String> problema = guardarSeleccion(carnet, anteriores, propuestas);
        if (problema.isPresent()) return error(problema.get());
        String nombreJornada = propuestas.getFirst().jornada;
        return Map.of("ok", true, "jornada", nombreJornada,
            "mensaje", "Horario " + nombreJornada + " asignado exitosamente (" + propuestas.size() + " asignaturas).");
    }

    public synchronized Map<String, Object> retirarTodosLosCursos(int carnet) {
        if (estudiantes.buscarPorCarnet(carnet) == null) return error("Estudiante no encontrado.");
        List<Integer> anteriores = asignaciones.obtenerAsignacionesPorCarnet(carnet);
        Optional<String> problema = guardarSeleccion(carnet, anteriores, List.of());
        if (problema.isPresent()) return error(problema.get());
        return Map.of("ok", true, "mensaje", "Se han retirado todas las asignaturas correctamente.");
    }

    public synchronized Map<String, Object> retirarCurso(int carnet, Map<String, Object> req) {
        if (estudiantes.buscarPorCarnet(carnet) == null) return error("Estudiante no encontrado.");
        Integer id = idSeccion(req);
        if (id == null) return error("Debe especificar un identificador de sección válido.");
        List<Integer> anteriores = asignaciones.obtenerAsignacionesPorCarnet(carnet);
        if (!anteriores.contains(id)) return error("El estudiante no tiene asignada esa sección.");
        List<SeccionCatalogo> propuestas = secciones(anteriores).stream().filter(s -> s.idSeccion != id).toList();
        Optional<String> problema = guardarSeleccion(carnet, anteriores, propuestas);
        if (problema.isPresent()) return error(problema.get());
        SeccionCatalogo retirada = catalogo.buscarSeccionCatalogo(id);
        return Map.of("ok", true, "mensaje", "Curso retirado exitosamente: "
            + (retirada == null ? "Sección " + id : retirada.nombreCurso));
    }

    private Optional<String> guardarSeleccion(int carnet, List<Integer> idsAnteriores,
                                               List<SeccionCatalogo> propuestas) {
        List<SeccionCatalogo> anteriores = secciones(idsAnteriores);
        List<SeccionCatalogo> seleccion = List.copyOf(propuestas);
        for (ReglaAsignacion regla : reglas) {
            Optional<String> problema = regla.validar(anteriores, seleccion);
            if (problema.isPresent()) return problema;
        }
        List<Integer> nuevosIds = seleccion.stream().map(s -> s.idSeccion).toList();
        // La escritura ocurre antes de modificar cupos; un error no deja una selección parcial.
        asignaciones.guardarAsignaciones(carnet, nuevosIds);
        anteriores.stream().filter(s -> !nuevosIds.contains(s.idSeccion)).forEach(SeccionCatalogo::liberarCupo);
        seleccion.stream().filter(s -> !idsAnteriores.contains(s.idSeccion)).forEach(SeccionCatalogo::reservarCupo);
        return Optional.empty();
    }

    private List<SeccionCatalogo> secciones(List<Integer> ids) {
        return ids.stream().map(catalogo::buscarSeccionCatalogo).filter(Objects::nonNull).toList();
    }

    private Integer idSeccion(Map<String, Object> req) {
        try { return Integer.valueOf(String.valueOf(req.get("idSeccion"))); }
        catch (NumberFormatException e) { return null; }
    }

    private Map<String, Object> error(String mensaje) { return Map.of("ok", false, "mensaje", mensaje); }
}
