package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Curso;
import java.util.ArrayList;
import java.util.List;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CsvCursoRepository implements CursoRepository {
    private static final String ARCHIVO = "cursos.csv";
    private final List<Curso> listaCursos = new ArrayList<>();


    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            listaCursos.add(new Curso(col[0], CsvUtil.leerEntero(col[1], 0), null));
        }
    }

    @Override
    public void sincronizar() {
        List<String[]> filas = new ArrayList<>();
        for (Curso c : listaCursos) {
            filas.add(new String[]{CsvUtil.escapar(c.getNombre()), String.valueOf(c.getCreditos())});
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @Override
    public List<Curso> obtenerTodos() { return List.copyOf(listaCursos); }

    @Override
    public void agregar(Curso entidad) { listaCursos.add(entidad); }

    @Override
    public Curso buscarPorNombre(String nombre) {
        for (Curso c : listaCursos) {
            if (c.getNombre().equalsIgnoreCase(nombre)) {
                return c;
            }
        }
        return null;
    }
}
