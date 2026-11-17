package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Seccion;
import java.util.List;

/** Persistencia de Seccion: consultas sin efectos laterales y escritura explicita. */
public interface SeccionRepository {
    List<Seccion> obtenerTodos();
    Seccion buscarPorId(int idSeccion);
    void agregar(Seccion entidad);
    void sincronizar();
}
