package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.DepartamentoRequest;
import com.uvg.pooproyect.dto.Requests.FacultadRequest;
import com.uvg.pooproyect.model.Carrera;
import com.uvg.pooproyect.model.Departamento;
import com.uvg.pooproyect.model.Facultad;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facultades")
public class FacultadController {

    private static final String ARCHIVO = "facultades.csv";

    private final List<Facultad> listaFacultades = new ArrayList<>();
    private final CarreraController carreraController;

    public FacultadController(CarreraController carreraController) {
        this.carreraController = carreraController;
    }

    // Los departamentos y las carreras asociadas se guardan codificados en
    // una columna cada uno, separados por ";" (mismo criterio que las
    // secciones: mantener un solo archivo simple por entidad).
    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Facultad facultad = new Facultad(col[0]);
            if (col.length > 1 && !col[1].isBlank()) {
                for (String depto : col[1].split(";")) {
                    facultad.agregarDepartamento(depto);
                }
            }
            if (col.length > 2 && !col[2].isBlank()) {
                for (String nombreCarrera : col[2].split(";")) {
                    Carrera carrera = carreraController.buscarPorNombre(nombreCarrera);
                    if (carrera != null) {
                        facultad.agregarCarrera(carrera);
                    }
                }
            }
            listaFacultades.add(facultad);
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Facultad f : listaFacultades) {
            StringBuilder deptos = new StringBuilder();
            for (Departamento d : f.getDepartamentos()) {
                if (deptos.length() > 0) deptos.append(";");
                deptos.append(d.getNombre());
            }
            StringBuilder carreras = new StringBuilder();
            for (Carrera c : f.getCarreras()) {
                if (carreras.length() > 0) carreras.append(";");
                carreras.append(c.getNombre());
            }
            filas.add(new String[]{CsvUtil.escapar(f.getNombre()), deptos.toString(), carreras.toString()});
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @PostMapping
    public Map<String, String> crearFacultad(@RequestBody FacultadRequest req) {
        listaFacultades.add(new Facultad(req.nombre()));
        guardar();
        return Map.of("mensaje", "Facultad '" + req.nombre() + "' creada.");
    }

    @PostMapping("/{nombreFacultad}/departamentos")
    public Map<String, String> agregarDepartamento(@PathVariable String nombreFacultad, @RequestBody DepartamentoRequest req) {
        Facultad facultad = buscarPorNombre(nombreFacultad);
        if (facultad == null) {
            return Map.of("mensaje", "Error: facultad '" + nombreFacultad + "' no encontrada.");
        }
        facultad.agregarDepartamento(req.nombreDepartamento());
        guardar();
        return Map.of("mensaje", "Departamento '" + req.nombreDepartamento() + "' agregado a " + nombreFacultad);
    }

    @PostMapping("/{nombreFacultad}/carreras/{nombreCarrera}")
    public Map<String, String> asociarCarrera(@PathVariable String nombreFacultad, @PathVariable String nombreCarrera) {
        Facultad facultad = buscarPorNombre(nombreFacultad);
        Carrera carrera = carreraController.buscarPorNombre(nombreCarrera);
        if (facultad == null) {
            return Map.of("mensaje", "Error: facultad '" + nombreFacultad + "' no encontrada.");
        }
        if (carrera == null) {
            return Map.of("mensaje", "Error: carrera '" + nombreCarrera + "' no encontrada.");
        }
        facultad.agregarCarrera(carrera);
        guardar();
        return Map.of("mensaje", "Carrera '" + nombreCarrera + "' asociada a la facultad " + nombreFacultad);
    }

    @GetMapping
    public List<Facultad> obtenerTodas() {
        return listaFacultades;
    }

    public Facultad buscarPorNombre(String nombre) {
        for (Facultad f : listaFacultades) {
            if (f.getNombre().equalsIgnoreCase(nombre)) {
                return f;
            }
        }
        return null;
    }
}
