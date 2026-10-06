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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private static final String ARCHIVO = "carreras.csv";

    private final List listaCarreras = new ArrayList<>();
    private final CursoController cursoController;

    public CarreraController(CursoController cursoController) {
        this.cursoController = cursoController;
    }

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
        List filas = new ArrayList<>();
        for (Carrera c : listaCarreras) {
            String planesEncoded = c.getPlanesDeEstudios().stream()
                    .map(p -> {
                        String cursosEncoded = p.getCursos().stream()
                                .map(Curso::getNombre)
                                .collect(Collectors.joining("|"));
                        return p.getYear() + ":" + p.getEstado() + ":" + cursosEncoded;
                    })
                    .collect(Collectors.joining(";"));

            filas.add(new String[]{
                    CsvUtil.escapar(c.getNombre()), 
                    CsvUtil.escapar(c.getTotalCreditos()),
                    String.valueOf(c.getDuracionPromedio()), 
                    planesEncoded
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @PostMapping
    public Map crearCarrera(@RequestBody CarreraRequest req) {
        listaCarreras.add(new Carrera(req.nombre(), req.totalCreditos(), req.duracionPromedio()));
        guardar();
        return Map.of("mensaje", "Carrera '" + req.nombre() + "' creada.");
    }

    @PostMapping("/{nombreCarrera}/planes")
    public Map agregarPlan(@PathVariable String nombreCarrera, @RequestBody PlanDeEstudioRequest req) {
        Carrera carrera = buscarPorNombre(nombreCarrera);
        if (carrera == null) {
            return Map.of("mensaje", "Error: carrera '" + nombreCarrera + "' no encontrada.");
        }
        carrera.agregarPlanDeEstudio(req.year(), req.estado());
        guardar();
        return Map.of("mensaje", "Plan de estudio " + req.year() + " agregado a " + nombreCarrera);
    }

    @PostMapping("/{nombreCarrera}/planes/{year}/cursos/{nombreCurso}")
    public Map agregarCursoAPlan(@PathVariable String nombreCarrera, @PathVariable int year, @PathVariable String nombreCurso) {
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

        Curso curso = cursoController.buscarPorNombre(nombreCurso);
        if (curso == null) {
            return Map.of("mensaje", "Error: curso '" + nombreCurso + "' no encontrado.");
        }

        plan.agregarCurso(curso);
        guardar();
        return Map.of("mensaje", "Curso '" + nombreCurso + "' agregado al plan " + year + " de " + nombreCarrera);
    }

    @GetMapping
    public List obtenerTodas() {
        return listaCarreras;
    }

    public Carrera buscarPorNombre(String nombre) {
        return listaCarreras.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }
}