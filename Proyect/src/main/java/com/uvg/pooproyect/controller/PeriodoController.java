package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.PeriodoRequest;
import com.uvg.pooproyect.model.PeriodoAcademico;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/periodos")
public class PeriodoController {

    private static final String ARCHIVO = "periodos.csv";

    private final List<PeriodoAcademico> listaPeriodos = new ArrayList<>();

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            listaPeriodos.add(new PeriodoAcademico(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], col[3], col[4], col[5], col[6], null
            ));
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (PeriodoAcademico p : listaPeriodos) {
            filas.add(new String[]{
                    String.valueOf(p.getIdPeriodo()), CsvUtil.escapar(p.getNombre()),
                    CsvUtil.escapar(p.getFechaInicio()), CsvUtil.escapar(p.getFechaFin()),
                    CsvUtil.escapar(p.getFechaInicioInscripcion()), CsvUtil.escapar(p.getFechaFinInscripcion()),
                    CsvUtil.escapar(p.getEstado())
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @PostMapping
    public Map<String, String> crearPeriodo(@RequestBody PeriodoRequest req) {
        PeriodoAcademico nuevo = new PeriodoAcademico(
                req.idPeriodo(), req.nombre(), req.fechaInicio(), req.fechaFin(),
                req.fechaInicioInscripcion(), req.fechaFinInscripcion(), req.estado(), null
        );
        listaPeriodos.add(nuevo);
        guardar();
        return Map.of("mensaje", "Periodo '" + req.nombre() + "' creado.");
    }

    @PostMapping("/{idPeriodo}/abrir")
    public Map<String, String> abrirInscripciones(@PathVariable int idPeriodo) {
        PeriodoAcademico periodo = buscarPorId(idPeriodo);
        if (periodo == null) {
            return Map.of("mensaje", "Error: periodo " + idPeriodo + " no encontrado.");
        }
        periodo.abrirInscripciones();
        guardar();
        return Map.of("mensaje", "Inscripciones abiertas para " + periodo.getNombre());
    }

    @PostMapping("/{idPeriodo}/cerrar")
    public Map<String, String> cerrarInscripciones(@PathVariable int idPeriodo) {
        PeriodoAcademico periodo = buscarPorId(idPeriodo);
        if (periodo == null) {
            return Map.of("mensaje", "Error: periodo " + idPeriodo + " no encontrado.");
        }
        periodo.cerrarInscripciones();
        guardar();
        return Map.of("mensaje", "Inscripciones cerradas para " + periodo.getNombre());
    }

    @GetMapping
    public List<PeriodoAcademico> obtenerTodos() {
        return listaPeriodos;
    }

    public PeriodoAcademico buscarPorId(int idPeriodo) {
        for (PeriodoAcademico p : listaPeriodos) {
            if (p.getIdPeriodo() == idPeriodo) {
                return p;
            }
        }
        return null;
    }
}
