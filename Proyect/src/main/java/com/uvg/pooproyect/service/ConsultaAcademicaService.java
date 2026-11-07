package com.uvg.pooproyect.service;

import com.uvg.pooproyect.dto.Requests.EstudianteRequest;
import com.uvg.pooproyect.dto.RespuestaAcademica.*;
import com.uvg.pooproyect.model.*;
import com.uvg.pooproyect.repository.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ConsultaAcademicaService {
    private final EstudianteRepository estudianteRepository;
    private final AsignacionRepository asignacionRepository;
    private final CatalogoSeccionRepository catalogoRepository;
    private final NotasRepository notasRepository;
    private final HistorialRepository historialRepository;

    public ConsultaAcademicaService(EstudianteRepository estudianteRepository, AsignacionRepository asignacionRepository, CatalogoSeccionRepository catalogoRepository, NotasRepository notasRepository, HistorialRepository historialRepository) {
        this.estudianteRepository = estudianteRepository;
        this.asignacionRepository = asignacionRepository;
        this.catalogoRepository = catalogoRepository;
        this.notasRepository = notasRepository;
        this.historialRepository = historialRepository;
    }

    public Map<String, Object> obtenerDashboard(int carnetFinal) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return null;
        }

        List<Integer> idsAsignados = asignacionRepository.obtenerAsignacionesPorCarnet(carnetFinal);
        List<Map<String, Object>> cursosActuales = new ArrayList<>();
        int totalCreditos = 0;

        for (Integer idSec : idsAsignados) {
            SeccionCatalogo sec = catalogoRepository.buscarSeccionCatalogo(idSec);
            if (sec != null) {
                totalCreditos += sec.creditos;
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("idSeccion", sec.idSeccion);
                map.put("codigoCurso", sec.codigoCurso);
                map.put("nombreCurso", sec.nombreCurso);
                map.put("codigoSeccion", sec.codigoSeccion);
                map.put("profesor", sec.profesor);
                map.put("horarios", sec.horarios);
                map.put("jornada", sec.jornada);
                map.put("salon", sec.salon);
                map.put("creditos", sec.creditos);
                map.put("color", sec.color);
                map.put("estado", "Inscrito");
                cursosActuales.add(map);
            }
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("carnet", est.getCarnet());
        resp.put("nombre", est.getNombre() + " " + est.getApellido());
        resp.put("carrera", "Ingeniería en Ciencias de la Computación");
        resp.put("facultad", "Facultad de Ingeniería");
        resp.put("periodoActual", "Segundo Semestre 2026");
        resp.put("cantidadCursos", cursosActuales.size());
        resp.put("totalCreditos", totalCreditos);
        resp.put("cursosActuales", cursosActuales);

        return resp;
    }

    public Map<String, Object> obtenerCursosDisponibles(int carnetFinal) {
        List<Integer> idsAsignados = asignacionRepository.obtenerAsignacionesPorCarnet(carnetFinal);
        Map<String, List<SeccionCatalogo>> porCurso = new LinkedHashMap<>();
        for (SeccionCatalogo sec : catalogoRepository.obtenerCatalogoSecciones()) {
            porCurso.computeIfAbsent(sec.codigoCurso, k -> new ArrayList<>()).add(sec);
        }

        List<Map<String, Object>> cursosResp = new ArrayList<>();
        for (Map.Entry<String, List<SeccionCatalogo>> entry : porCurso.entrySet()) {
            List<SeccionCatalogo> secs = entry.getValue();
            SeccionCatalogo first = secs.get(0);

            List<Map<String, Object>> seccionesList = new ArrayList<>();
            for (SeccionCatalogo s : secs) {
                Map<String, Object> sm = new LinkedHashMap<>();
                sm.put("idSeccion", s.idSeccion);
                sm.put("codigo", s.codigoSeccion);
                sm.put("jornada", s.jornada);
                sm.put("profesor", s.profesor);
                sm.put("horarios", s.horarios);
                sm.put("salon", s.salon);
                sm.put("color", s.color);
                sm.put("cupoMaximo", s.cupoMaximo);
                sm.put("cupoActual", s.getCupoActual());
                sm.put("asignada", idsAsignados.contains(s.idSeccion));
                seccionesList.add(sm);
            }

            Map<String, Object> cursoMap = new LinkedHashMap<>();
            cursoMap.put("codigo", first.codigoCurso);
            cursoMap.put("nombre", first.nombreCurso);
            cursoMap.put("creditos", first.creditos);
            cursoMap.put("carrera", "Ingeniería en Ciencias de la Computación");
            cursoMap.put("secciones", seccionesList);
            cursosResp.add(cursoMap);
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("periodo", "Segundo Semestre 2026");
        resp.put("cursos", cursosResp);

        return resp;
    }

    public Map<String, Object> obtenerHorario(int carnetFinal) {
        List<Integer> idsAsignados = asignacionRepository.obtenerAsignacionesPorCarnet(carnetFinal);
        List<Map<String, Object>> bloquesResp = new ArrayList<>();

        for (Integer idSec : idsAsignados) {
            SeccionCatalogo sec = catalogoRepository.buscarSeccionCatalogo(idSec);
            if (sec != null && sec.bloques != null) {
                for (BloqueHorario b : sec.bloques) {
                    Map<String, Object> bm = new LinkedHashMap<>();
                    bm.put("idSeccion", sec.idSeccion);
                    bm.put("codigoCurso", sec.codigoCurso);
                    bm.put("nombreCurso", sec.nombreCurso);
                    bm.put("codigoSeccion", sec.codigoSeccion);
                    bm.put("profesor", sec.profesor);
                    bm.put("dia", b.dia());
                    bm.put("horaInicio", b.horaInicio());
                    bm.put("horaFin", b.horaFin());
                    bm.put("salon", b.salon());
                    bm.put("color", sec.color);
                    bm.put("jornada", sec.jornada);
                    bloquesResp.add(bm);
                }
            }
        }

        Map<String, Object> recesoMap = new LinkedHashMap<>();
        recesoMap.put("horaInicio", "10:15");
        recesoMap.put("horaFin", "10:40");
        recesoMap.put("descripcion", "Receso Académico UVG");
        recesoMap.put("dias", List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes"));

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("periodo", "Segundo Semestre 2026");
        resp.put("receso", recesoMap);
        resp.put("bloques", bloquesResp);

        return resp;
    }

    public Map<String, Object> obtenerNotas(int carnetFinal) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return null;
        }

        List<Integer> idsAsignados = asignacionRepository.obtenerAsignacionesPorCarnet(carnetFinal);
        List<NotaCurso> notas = new ArrayList<>();

        List<String> codigosActivos = new ArrayList<>();
        Map<String, SeccionCatalogo> mapaSeccion = new LinkedHashMap<>();
        for (Integer idSec : idsAsignados) {
            SeccionCatalogo sec = catalogoRepository.buscarSeccionCatalogo(idSec);
            if (sec != null) {
                codigosActivos.add(sec.codigoCurso);
                mapaSeccion.put(sec.codigoCurso, sec);
            }
        }

        List<NotaCurso> catalogoNotas = notasRepository.obtenerNotas(carnetFinal);
        for (NotaCurso nc : catalogoNotas) {
            if (codigosActivos.isEmpty() || codigosActivos.contains(nc.codigoCurso)) {
                if (mapaSeccion.containsKey(nc.codigoCurso)) {
                    SeccionCatalogo s = mapaSeccion.get(nc.codigoCurso);
                    nc.seccion = "Sec. " + s.codigoSeccion + " (" + s.jornada + ")";
                    nc.profesor = s.profesor;
                }
                notas.add(nc);
            }
        }

        double sumaPonderada = 0;
        int totalCreditos = 0;
        for (NotaCurso n : notas) {
            sumaPonderada += (n.zonaObtenida / n.zonaTotal * 100.0) * n.creditos;
            totalCreditos += n.creditos;
        }
        double promedioZona = totalCreditos > 0 ? Math.round((sumaPonderada / totalCreditos) * 10.0) / 10.0 : 0.0;

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("carnet", est.getCarnet());
        resp.put("estudiante", est.getNombre() + " " + est.getApellido());
        resp.put("periodo", "Segundo Semestre 2026");
        resp.put("promedioZona", promedioZona);
        resp.put("totalCreditos", totalCreditos);
        resp.put("totalCursos", notas.size());
        resp.put("cursos", notas);

        return resp;
    }

    public Map<String, Object> obtenerHistorial(int carnetFinal) {
        Estudiante est = estudianteRepository.buscarPorCarnet(carnetFinal);
        if (est == null) {
            return null;
        }

        List<SemestreHistorial> semestres = historialRepository.obtenerSemestres(carnetFinal);

        int creditosTotales = 0;
        int cursosAprobados = 0;
        double sumaNotasPonderadas = 0;
        for (SemestreHistorial s : semestres) {
            for (CursoHistorial c : s.cursos()) {
                if ("Aprobado".equalsIgnoreCase(c.estado())) {
                    creditosTotales += c.creditos();
                    cursosAprobados++;
                    sumaNotasPonderadas += (c.notaFinal() * c.creditos());
                }
            }
        }
        double promedioAcumulado = creditosTotales > 0 ? Math.round((sumaNotasPonderadas / creditosTotales) * 10.0) / 10.0 : 0.0;

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("carnet", est.getCarnet());
        resp.put("estudiante", est.getNombre() + " " + est.getApellido());
        resp.put("carrera", "Ingeniería en Ciencias de la Computación");
        resp.put("facultad", "Facultad de Ingeniería");
        resp.put("promedioAcumulado", promedioAcumulado);
        resp.put("creditosAprobados", creditosTotales);
        resp.put("cursosAprobados", cursosAprobados);
        resp.put("eficienciaAcademica", "100%");
        resp.put("semestres", semestres);

        return resp;
    }
}
