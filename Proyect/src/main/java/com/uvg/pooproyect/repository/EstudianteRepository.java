package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Estudiante;
import java.util.List;

public interface EstudianteRepository {

    record BloqueHorario(String dia, String horaInicio, String horaFin, String salon) {}

    class SeccionCatalogo {
        public int idSeccion;
        public int codigoSeccion;
        public String codigoCurso;
        public String nombreCurso;
        public int creditos;
        public String profesor;
        public String horarios;
        public String jornada;
        public String salon;
        public String color;
        public int cupoMaximo;
        public int cupoActual;
        public List<BloqueHorario> bloques;

        public SeccionCatalogo(int idSeccion, int codigoSeccion, String codigoCurso, String nombreCurso, int creditos,
                               String profesor, String horarios, String jornada, String salon, String color,
                               int cupoMaximo, int cupoActual, List<BloqueHorario> bloques) {
            this.idSeccion = idSeccion;
            this.codigoSeccion = codigoSeccion;
            this.codigoCurso = codigoCurso;
            this.nombreCurso = nombreCurso;
            this.creditos = creditos;
            this.profesor = profesor;
            this.horarios = horarios;
            this.jornada = jornada;
            this.salon = salon;
            this.color = color;
            this.cupoMaximo = cupoMaximo;
            this.cupoActual = cupoActual;
            this.bloques = bloques;
        }
    }

    List<Estudiante> obtenerTodos();

    Estudiante buscarPorCarnet(int carnet);

    Estudiante guardar(Estudiante estudiante);

    void sincronizarEstudiantes();

    List<Integer> obtenerAsignacionesPorCarnet(int carnet);

    void guardarAsignaciones(int carnet, List<Integer> asignaciones);

    List<SeccionCatalogo> obtenerCatalogoSecciones();

    SeccionCatalogo buscarSeccionCatalogo(int idSeccion);
}
