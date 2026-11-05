package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Profesor;
import java.util.List;

/** Persistencia de Profesor: consultas sin efectos laterales y escritura explicita. */
public interface ProfesorRepository {
    List<Profesor> obtenerTodos();
    Profesor buscarPorCodigo(int codigo);
    void agregar(Profesor entidad);
    void sincronizar();
}
