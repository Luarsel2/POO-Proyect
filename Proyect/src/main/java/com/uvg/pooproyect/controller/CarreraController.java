package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.CarreraRequest;
import com.uvg.pooproyect.dto.Requests.PlanDeEstudioRequest;
import com.uvg.pooproyect.model.Carrera;
import com.uvg.pooproyect.model.Curso;
import com.uvg.pooproyect.model.PlanDeEstudio;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private static final String ARCHIVO = "carreras.csv";

    private final List<Carrera> listaCarreras = new ArrayList<>();
    private final CursoController cursoController;

    public CarreraController(CursoController cursoController) {
        this.cursoController = cursoController;
    }

    // Los planes de estudio (y los cursos que contiene cada uno) se guardan
    // codificados en una sola columna: los planes separados por ";", y
    // dentro de cada plan "year:estado:curso1|curso2".
    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Carrera carrera = new Carrera(col[0], col[1], CsvUtil.leerEntero(col[2], 0));
            if (col.length > 3 && !col[3].isBlank()) {
                for (String bloquePlan : col[3].split(";")) {
                    String[] campos = bloquePlan.split(":", -1);
                    if (campos.length < 2) continue;
                    int year = CsvUtil.leerEntero(campos[0], 0);
                    String estado = campos[1];
                    carrera.agregarPlanDeEstudio(year, estado);
                    if (campos.length > 2 && !campos[2].isBlank()) {
                        PlanDeEstudio plan = carrera.getPlanesDeEstudios().get(carrera.getPlanesDeEstudios().size() - 1);
                        for (String nombreCurso : campos[2].split("\\|")) {
                            Curso curso = cursoController.buscarPorNombre(nombreCurso);
                            if (curso != null) {
                                plan.agregarCurso(curso);
                            }
                        }
                    }
                }
            }
            listaCarreras.add(carrera);
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Carrera c : listaCarreras) {
            StringBuilder planesEncoded = new StringBuilder();
            for (PlanDeEstudio p : c.getPlanesDeEstudios()) {
                if (planesEncoded.length() > 0) planesEncoded.append(";");
                StringBuilder cursosEncoded = new StringBuilder();
                for (Curso curso : p.getCursos()) {
                    if (cursosEncoded.length() > 0) cursosEncoded.append("|");
                    cursosEncoded.append(curso.getNombre());
                }
                planesEncoded.append(p.getYear()).append(":").append(p.getEstado()).append(":").append(cursosEncoded);
            }
            filas.add(new String[]{
                    CsvUtil.escapar(c.getNombre()), CsvUtil.escapar(c.getTotalCreditos()),
                    String.valueOf(c.getDuracionPromedio()), planesEncoded.toString()
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @PostMapping
    public Map<String, String> crearCarrera(@RequestBody CarreraRequest req) {
        listaCarreras.add(new Carrera(req.nombre(), req.totalCreditos(), req.duracionPromedio()));
        guardar();
        return Map.of("mensaje", "Carrera '" + req.nombre() + "' creada.");
    }

    @PostMapping("/{nombreCarrera}/planes")
    public Map<String, String> agregarPlan(@PathVariable String nombreCarrera, @RequestBody PlanDeEstudioRequest req) {
        Carrera carrera = buscarPorNombre(nombreCarrera);
        if (carrera == null) {
            return Map.of("mensaje", "Error: carrera '" + nombreCarrera + "' no encontrada.");
        }
        carrera.agregarPlanDeEstudio(req.year(), req.estado());
        guardar();
        return Map.of("mensaje", "Plan de estudio " + req.year() + " agregado a " + nombreCarrera);
    }

    @PostMapping("/{nombreCarrera}/planes/{year}/cursos/{nombreCurso}")
    public Map<String, String> agregarCursoAPlan(@PathVariable String nombreCarrera, @PathVariable int year, @PathVariable String nombreCurso) {
        Carrera carrera = buscarPorNombre(nombreCarrera);
        if (carrera == null) {
            return Map.of("mensaje", "Error: carrera '" + nombreCarrera + "' no encontrada.");
        }
        PlanDeEstudio plan = null;
        for (PlanDeEstudio p : carrera.getPlanesDeEstudios()) {
            if (p.getYear() == year) {
                plan = p;
                break;
            }
        }
        if (plan == null) {
            return Map.of("mensaje", "Error: no existe un plan del año " + year + " en " + nombreCarrera);
        }
        Curso curso = cursoController.buscarPorNombre(nombreCurso);
        if (curso == null) {
            return Map.of("mensaje", "Error: curso '" + nombreCurso + "' no encontrado.");
        }
        plan.agregarCurso(curso);
        guardar();
        return Map.of("mensaje", "Curso '" + nombreCurso + "' agregado al plan " + year + " de " + nombreCarrera);
    }

    @GetMapping
    public List<Carrera> obtenerTodas() {
        return listaCarreras;
    }

    public Carrera buscarPorNombre(String nombre) {
        for (Carrera c : listaCarreras) {
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }
}
