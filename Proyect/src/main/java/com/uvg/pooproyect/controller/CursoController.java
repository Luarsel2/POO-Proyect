package com.uvg.pooproyect.controller;

import com.uvg.pooproyect.dto.Requests.CursoRequest;
import com.uvg.pooproyect.model.Curso;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private static final String ARCHIVO = "cursos.csv";

    private final List<Curso> listaCursos = new ArrayList<>();

    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            listaCursos.add(new Curso(col[0], CsvUtil.leerEntero(col[1], 0), null));
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Curso c : listaCursos) {
            filas.add(new String[]{CsvUtil.escapar(c.getNombre()), String.valueOf(c.getCreditos())});
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @PostMapping
    public Map<String, String> crearCurso(@RequestBody CursoRequest req) {
        Curso nuevo = new Curso(req.nombre(), req.creditos(), null);
        listaCursos.add(nuevo);
        guardar();
        return Map.of("mensaje", "Curso '" + req.nombre() + "' creado con " + req.creditos() + " créditos.");
    }

    @GetMapping
    public List<Curso> obtenerTodos() {
        return listaCursos;
    }

    // usado internamente por CarreraController
    public Curso buscarPorNombre(String nombre) {
        for (Curso c : listaCursos) {
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }
}
