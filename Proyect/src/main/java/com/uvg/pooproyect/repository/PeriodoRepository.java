package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.PeriodoAcademico;
import java.util.List;

/** Persistencia de PeriodoAcademico: consultas sin efectos laterales y escritura explicita. */
public interface PeriodoRepository {
    List<PeriodoAcademico> obtenerTodos();
    PeriodoAcademico buscarPorId(int idPeriodo);
    void agregar(PeriodoAcademico entidad);
    void sincronizar();
}
