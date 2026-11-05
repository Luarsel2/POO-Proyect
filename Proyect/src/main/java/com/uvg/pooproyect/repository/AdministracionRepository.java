package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Administracion;
import java.util.List;

/** Persistencia de Administracion: consultas sin efectos laterales y escritura explicita. */
public interface AdministracionRepository {
    List<Administracion> obtenerTodos();
    Administracion buscarPorCodigo(int codigo);
    void agregar(Administracion entidad);
    void sincronizar();
}
