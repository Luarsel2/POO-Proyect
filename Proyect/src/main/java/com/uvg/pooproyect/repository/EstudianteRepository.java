package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.model.Estudiante;
import java.util.List;

/** Consultar un carnet inexistente devuelve null y nunca crea registros.
 * Las listas devueltas son instantáneas inmodificables; las entidades conservan su identidad.
 */
public interface EstudianteRepository {
    List<Estudiante> obtenerTodos();
    Estudiante buscarPorCarnet(int carnet);
    Estudiante guardar(Estudiante estudiante);
    void sincronizarEstudiantes();
}
