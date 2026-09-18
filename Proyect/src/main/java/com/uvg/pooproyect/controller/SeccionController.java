package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.HorarioRequest;
import com.uvg.pooproyect.dto.Requests.SeccionRequest;
import com.uvg.pooproyect.model.Horario;
import com.uvg.pooproyect.model.Profesor;
import com.uvg.pooproyect.model.Seccion;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/secciones")
public class SeccionController {

    private static final String ARCHIVO = "secciones.csv";

    private final List<Seccion> listaSecciones = new ArrayList<>();
    private final ProfesorController profesorController;

    public SeccionController(ProfesorController profesorController) {
        this.profesorController = profesorController;
    }

    // Los horarios de una sección se guardan codificados en una sola columna
    // del CSV, separados por ";" y con sus campos separados por ":", para no
    // necesitar un archivo aparte solo para horarios (mantiene el formato simple).
    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Profesor profesor = profesorController.buscarPorCodigo(CsvUtil.leerEntero(col[5], -1));
            Seccion seccion = new Seccion(
                    CsvUtil.leerEntero(col[0], 0), CsvUtil.leerEntero(col[1], 0),
                    CsvUtil.leerEntero(col[2], 0), CsvUtil.leerEntero(col[3], 0),
                    col[4], profesor, null
            );
            if (col.length > 6 && !col[6].isBlank()) {
                for (String bloque : col[6].split(";")) {
                    String[] campos = bloque.split(":", -1);
                    if (campos.length == 4) {
                        seccion.agregarHorario(CsvUtil.leerEntero(campos[0], 0), campos[1], campos[2], campos[3]);
                    }
                }
            }
            listaSecciones.add(seccion);
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Seccion s : listaSecciones) {
            StringBuilder horariosEncoded = new StringBuilder();
            for (Horario h : s.getHorario()) {
                if (horariosEncoded.length() > 0) horariosEncoded.append(";");
                horariosEncoded.append(h.getIdHorario()).append(":").append(h.getDiaSemana())
                        .append(":").append(h.getHoraInicio()).append(":").append(h.getHoraFin());
            }
            filas.add(new String[]{
                    String.valueOf(s.getIdSeccion()), String.valueOf(s.getCodigo()),
                    String.valueOf(s.getCupoMaximo()), String.valueOf(s.getCupoActual()),
                    CsvUtil.escapar(s.getEstado()),
                    s.getProfesor() != null ? String.valueOf(s.getProfesor().getCodigoDeEmpleado()) : "",
                    horariosEncoded.toString()
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @PostMapping
    public Map<String, String> crearSeccion(@RequestBody SeccionRequest req) {
        Profesor profesor = profesorController.buscarPorCodigo(req.codigoProfesor());
        Seccion nueva = new Seccion(req.idSeccion(), req.codigo(), req.cupoMaximo(), 0, "ACTIVA", profesor, null);
        listaSecciones.add(nueva);
        guardar();
        String msgProfesor = profesor != null
                ? " con profesor " + profesor.getNombre() + " " + profesor.getApellido()
                : " (sin profesor asignado, verifica el código)";
        return Map.of("mensaje", "Sección " + req.codigo() + " creada" + msgProfesor);
    }

    @PostMapping("/{idSeccion}/horarios")
    public Map<String, String> agregarHorario(@PathVariable int idSeccion, @RequestBody HorarioRequest req) {
        Seccion seccion = buscarPorId(idSeccion);
        if (seccion == null) {
            return Map.of("mensaje", "Error: sección " + idSeccion + " no encontrada.");
        }
        seccion.agregarHorario(req.idHorario(), req.diaSemana(), req.horaInicio(), req.horaFin());
        guardar();
        return Map.of("mensaje", "Horario agregado a la sección " + seccion.getCodigo());
    }

    @PostMapping("/{idSeccion}/inscribir")
    public Map<String, String> agregarEstudiante(@PathVariable int idSeccion) {
        Seccion seccion = buscarPorId(idSeccion);
        if (seccion == null) {
            return Map.of("mensaje", "Error: sección " + idSeccion + " no encontrada.");
        }
        boolean habiaCupo = seccion.verificarCupos();
        seccion.agregarEstudiante();
        guardar();
        return Map.of("mensaje", habiaCupo
                ? "Estudiante agregado. Cupos disponibles: " + seccion.obtenerCuposDisponibles()
                : "Sin cupos disponibles en esta sección.");
    }

    @PostMapping("/{idSeccion}/retirar")
    public Map<String, String> retirarEstudiante(@PathVariable int idSeccion) {
        Seccion seccion = buscarPorId(idSeccion);
        if (seccion == null) {
            return Map.of("mensaje", "Error: sección " + idSeccion + " no encontrada.");
        }
        seccion.retirarEstudiante();
        guardar();
        return Map.of("mensaje", "Estudiante retirado. Cupos disponibles: " + seccion.obtenerCuposDisponibles());
    }

    @GetMapping
    public List<Seccion> obtenerTodas() {
        return listaSecciones;
    }

    public Seccion buscarPorId(int idSeccion) {
        for (Seccion s : listaSecciones) {
            if (s.getIdSeccion() == idSeccion) {
                return s;
            }
        }
        return null;
    }
}
