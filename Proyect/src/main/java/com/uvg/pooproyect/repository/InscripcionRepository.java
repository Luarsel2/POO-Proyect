package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Inscripcion;
import java.util.List;

/** Persistencia de Inscripcion: consultas sin efectos laterales y escritura explicita. */
public interface InscripcionRepository {
    List<Inscripcion> obtenerTodos();
    Inscripcion buscarPorId(int id);
    void agregar(Inscripcion entidad);
    void sincronizar();
}
