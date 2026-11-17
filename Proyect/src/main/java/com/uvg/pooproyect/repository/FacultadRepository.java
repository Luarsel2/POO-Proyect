package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Facultad;
import java.util.List;

/** Persistencia de Facultad: consultas sin efectos laterales y escritura explicita. */
public interface FacultadRepository {
    List<Facultad> obtenerTodos();
    Facultad buscarPorNombre(String nombre);
    void agregar(Facultad entidad);
    void sincronizar();
}
