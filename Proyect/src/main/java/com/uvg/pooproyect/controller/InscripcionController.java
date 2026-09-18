package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.DetalleInscripcionRequest;
import com.uvg.pooproyect.dto.Requests.InscripcionRequest;
import com.uvg.pooproyect.model.DetalleInscripcion;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.model.Inscripcion;
import com.uvg.pooproyect.model.PeriodoAcademico;
import com.uvg.pooproyect.model.Seccion;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Version REST del InscripcionController original (procesarInscripcion),
// ampliado para poder crear la Inscripcion completa: se asocia al
// Estudiante y al PeriodoAcademico, y despues se le pueden ir agregando
// detalles (secciones) una por una. Los detalles se guardan codificados en
// una columna del CSV, igual que los horarios de Seccion.
@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private static final String ARCHIVO = "inscripciones.csv";

    private final List<Inscripcion> listaInscripciones = new ArrayList<>();
    private final EstudianteController estudianteController;
    private final PeriodoController periodoController;
    private final SeccionController seccionController;

    public InscripcionController(EstudianteController estudianteController,
                                  PeriodoController periodoController,
                                  SeccionController seccionController) {
        this.estudianteController = estudianteController;
        this.periodoController = periodoController;
        this.seccionController = seccionController;
    }

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Estudiante estudiante = estudianteController.buscarPorCarnet(CsvUtil.leerEntero(col[4], -1));
            PeriodoAcademico periodo = periodoController.buscarPorId(CsvUtil.leerEntero(col[5], -1));
            if (estudiante == null || periodo == null) {
                // El estudiante o el período ya no existen; se omite esta inscripción guardada.
                continue;
            }
            Inscripcion inscripcion = new Inscripcion(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], CsvUtil.leerEntero(col[3], 0),
                    estudiante, periodo
            );
            if (col.length > 6 && !col[6].isBlank()) {
                for (String bloque : col[6].split(";")) {
                    String[] campos = bloque.split(":", -1);
                    if (campos.length != 4) continue;
                    Seccion seccion = seccionController.buscarPorId(CsvUtil.leerEntero(campos[3], -1));
                    if (seccion != null) {
                        inscripcion.agregarDetalle(CsvUtil.leerEntero(campos[0], 0), campos[1], campos[2], seccion);
                    }
                }
            }
            listaInscripciones.add(inscripcion);
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Inscripcion i : listaInscripciones) {
            StringBuilder detallesEncoded = new StringBuilder();
            for (DetalleInscripcion d : i.getDetalles()) {
                if (detallesEncoded.length() > 0) detallesEncoded.append(";");
                detallesEncoded.append(d.getIdDetalle()).append(":").append(d.getFechaInscripcion())
                        .append(":").append(d.getEstado()).append(":").append(d.getSeccion().getIdSeccion());
            }
            filas.add(new String[]{
                    String.valueOf(i.getId()), CsvUtil.escapar(i.getFechaDeInscripcion()),
                    CsvUtil.escapar(i.getEstado()), String.valueOf(i.getTotalCreditos()),
                    String.valueOf(i.getEstudiante().getCarnet()),
                    String.valueOf(i.getPeriodoAcademico().getIdPeriodo()),
                    detallesEncoded.toString()
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    // Equivalente al procesarInscripcion original, pero armando la
    // Inscripcion completa a partir del carnet y el periodo academico.
    @PostMapping
    public Map<String, String> procesarInscripcion(@RequestBody InscripcionRequest req) {
        Estudiante estudiante = estudianteController.buscarPorCarnet(req.carnetEstudiante());
        PeriodoAcademico periodo = periodoController.buscarPorId(req.idPeriodo());

        if (estudiante == null) {
            return Map.of("mensaje", "Error: estudiante con carnet " + req.carnetEstudiante() + " no encontrado.");
        }
        if (periodo == null) {
            return Map.of("mensaje", "Error: periodo con id " + req.idPeriodo() + " no encontrado.");
        }

        Inscripcion inscripcion = new Inscripcion(
                req.id(), req.fechaDeInscripcion(), req.estado(), req.totalCreditos(), estudiante, periodo
        );
        listaInscripciones.add(inscripcion);
        guardar();

        return Map.of("mensaje", "Inscripción completada para " + estudiante.getNombre()
                + " en el periodo " + periodo.getNombre());
    }

    @PostMapping("/{idInscripcion}/detalles")
    public Map<String, String> agregarDetalle(@PathVariable int idInscripcion, @RequestBody DetalleInscripcionRequest req) {
        Inscripcion inscripcion = buscarPorId(idInscripcion);
        if (inscripcion == null) {
            return Map.of("mensaje", "Error: inscripción " + idInscripcion + " no encontrada.");
        }
        Seccion seccion = seccionController.buscarPorId(req.idSeccion());
        if (seccion == null) {
            return Map.of("mensaje", "Error: sección " + req.idSeccion() + " no encontrada.");
        }
        inscripcion.agregarDetalle(req.idDetalle(), req.fechaInscripcion(), req.estado(), seccion);
        guardar();
        return Map.of("mensaje", "Sección " + seccion.getCodigo() + " agregada a la inscripción " + idInscripcion);
    }

    @GetMapping
    public List<Inscripcion> obtenerTodas() {
        return listaInscripciones;
    }

    public Inscripcion buscarPorId(int id) {
        for (Inscripcion i : listaInscripciones) {
            if (i.getId() == id) {
                return i;
            }
        }
        return null;
    }
}
