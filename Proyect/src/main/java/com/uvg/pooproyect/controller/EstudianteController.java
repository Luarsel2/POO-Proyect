package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.EstudianteRequest;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.service.EstudianteService;
import com.uvg.pooproyect.service.ConsultaAcademicaService;
import com.uvg.pooproyect.service.AsignacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;
    private final ConsultaAcademicaService consultaService;
    private final AsignacionService asignacionService;

    public EstudianteController(EstudianteService estudianteService, ConsultaAcademicaService consultaService, AsignacionService asignacionService) {
        this.consultaService = consultaService;
        this.asignacionService = asignacionService;
        this.estudianteService = estudianteService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> registrarEstudiante(@RequestBody EstudianteRequest req) {
        return ResponseEntity.ok(estudianteService.registrarEstudiante(req));
    }

    @PostMapping("/{carnet}/inscribir-curso")
    public ResponseEntity<Map<String, String>> inscribirCursoAEstudiante(@PathVariable("carnet") int carnet) {
        return ResponseEntity.ok(estudianteService.inscribirCurso(carnet));
    }

    @GetMapping("/{carnet}")
    public Estudiante buscarPorCarnet(@PathVariable("carnet") int carnet) {
        return estudianteService.buscarPorCarnet(carnet);
    }

    @GetMapping
    public List<Estudiante> obtenerTodos() {
        return estudianteService.obtenerTodos();
    }

    @GetMapping({"/dashboard", "/{carnet}/dashboard"})
    public ResponseEntity<Map<String, Object>> obtenerDashboardEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        Map<String, Object> resp = consultaService.obtenerDashboard(resolverCarnet(carnet, sesion));
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping({"/cursos-disponibles", "/{carnet}/cursos-disponibles"})
    public ResponseEntity<Map<String, Object>> obtenerCursosDisponibles(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        return ResponseEntity.ok(consultaService.obtenerCursosDisponibles(resolverCarnet(carnet, sesion)));
    }

    @PostMapping({"/asignar-curso", "/{carnet}/asignar-curso"})
    public ResponseEntity<Map<String, Object>> asignarCurso(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            @RequestBody Map<String, Object> req,
            HttpSession sesion) {
        Map<String, Object> res = asignacionService.asignarCurso(resolverCarnet(carnet, sesion), req);
        if (Boolean.FALSE.equals(res.get("ok"))) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping({"/asignar-horario", "/{carnet}/asignar-horario"})
    public ResponseEntity<Map<String, Object>> asignarHorarioCompleto(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            @RequestBody Map<String, Object> req,
            HttpSession sesion) {
        Map<String, Object> res = asignacionService.asignarHorarioCompleto(resolverCarnet(carnet, sesion), req);
        if (Boolean.FALSE.equals(res.get("ok"))) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping({"/retirar-todos", "/{carnet}/retirar-todos"})
    public ResponseEntity<Map<String, Object>> retirarTodosLosCursos(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        Map<String, Object> res = asignacionService.retirarTodosLosCursos(resolverCarnet(carnet, sesion));
        if (Boolean.FALSE.equals(res.get("ok"))) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping({"/retirar-curso", "/{carnet}/retirar-curso"})
    public ResponseEntity<Map<String, Object>> retirarCurso(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            @RequestBody Map<String, Object> req,
            HttpSession sesion) {
        Map<String, Object> res = asignacionService.retirarCurso(resolverCarnet(carnet, sesion), req);
        if (Boolean.FALSE.equals(res.get("ok"))) {
            return ResponseEntity.badRequest().body(res);
        }
        return ResponseEntity.ok(res);
    }

    @GetMapping({"/horario", "/{carnet}/horario"})
    public ResponseEntity<Map<String, Object>> obtenerHorarioEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        return ResponseEntity.ok(consultaService.obtenerHorario(resolverCarnet(carnet, sesion)));
    }

    @GetMapping({"/notas", "/{carnet}/notas"})
    public ResponseEntity<Map<String, Object>> obtenerNotasEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        Map<String, Object> resp = consultaService.obtenerNotas(resolverCarnet(carnet, sesion));
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping({"/historial", "/{carnet}/historial"})
    public ResponseEntity<Map<String, Object>> obtenerHistorialEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        Map<String, Object> resp = consultaService.obtenerHistorial(resolverCarnet(carnet, sesion));
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping({"/perfil", "/{carnet}/perfil"})
    public ResponseEntity<Map<String, Object>> obtenerPerfilEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            HttpSession sesion) {
        Map<String, Object> resp = estudianteService.obtenerPerfil(resolverCarnet(carnet, sesion));
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    @PutMapping({"/perfil", "/{carnet}/perfil"})
    public ResponseEntity<Map<String, Object>> actualizarPerfilEstudiante(
            @PathVariable(value = "carnet", required = false) Integer carnet,
            @RequestBody Map<String, Object> datos,
            HttpSession sesion) {
        Map<String, Object> resp = estudianteService.actualizarPerfil(resolverCarnet(carnet, sesion), datos);
        if (resp == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resp);
    }

    private int resolverCarnet(Integer carnet, HttpSession sesion) {
        if (carnet != null && carnet > 0) {
            return carnet;
        }
        if (sesion != null) {
            Object sc = sesion.getAttribute("carnet");
            if (sc != null) {
                try {
                    return Integer.parseInt(sc.toString());
                } catch (Exception ignored) {}
            }
        }
        return 26547;
    }
}
