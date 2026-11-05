package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Carrera;
import java.util.List;

/** Persistencia de Carrera: consultas sin efectos laterales y escritura explicita. */
public interface CarreraRepository {
    List<Carrera> obtenerTodos();
    Carrera buscarPorNombre(String nombre);
    void agregar(Carrera entidad);
    void sincronizar();
}
