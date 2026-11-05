package com.uvg.pooproyect.repository;
import java.util.List;

/** Las consultas devuelven copias; guardar reemplaza la selección de un estudiante. */
public interface AsignacionRepository {
    List<Integer> obtenerAsignacionesPorCarnet(int carnet);
    void guardarAsignaciones(int carnet, List<Integer> asignaciones);
}
