package com.uvg.pooproyect.model;

import java.util.List;

public class SeccionCatalogo {
    public final int idSeccion;
    public final int codigoSeccion;
    public final String codigoCurso;
    public final String nombreCurso;
    public final int creditos;
    public final String profesor;
    public final String horarios;
    public final String jornada;
    public final String salon;
    public final String color;
    public final int cupoMaximo;
    private int cupoActual;
    private final int cupoBase;
    public final List<BloqueHorario> bloques;

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
        this.cupoBase = cupoActual;
        this.bloques = List.copyOf(bloques);
    }
    public void restaurarAsignaciones(int cantidad) {
        if (cantidad < 0 || cupoBase + cantidad > cupoMaximo) {
            throw new IllegalStateException("Las asignaciones guardadas exceden el cupo de la sección " + idSeccion);
        }
        cupoActual = cupoBase + cantidad;
    }

    public int getCupoActual() { return cupoActual; }

    public void reservarCupo() {
        if (cupoActual >= cupoMaximo) throw new IllegalStateException("No hay cupos disponibles.");
        cupoActual++;
    }

    public void liberarCupo() { if (cupoActual > 0) cupoActual--; }
}
