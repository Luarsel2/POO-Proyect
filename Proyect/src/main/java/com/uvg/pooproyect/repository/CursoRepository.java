package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Curso;
import java.util.List;

/** Persistencia de Curso: consultas sin efectos laterales y escritura explicita. */
public interface CursoRepository {
    List<Curso> obtenerTodos();
    Curso buscarPorNombre(String nombre);
    void agregar(Curso entidad);
    void sincronizar();
}
