package com.uvg.pooproyect.dto;
import java.util.List;

public final class RespuestaAcademica {
    private RespuestaAcademica() {}
    public record ActividadNota(String nombre, double ponderacion, double obtenido, String fecha, String retroalimentacion) {}

    public static class NotaCurso {
        public String codigoCurso;
        public String nombreCurso;
        public int creditos;
        public String seccion;
        public String profesor;
        public String color;
        public double zonaTotal;
        public double zonaObtenida;
        public double examenFinal;
        public double notaTotal;
        public String estado;
        public List<ActividadNota> desglose;

        public NotaCurso(String codigoCurso, String nombreCurso, int creditos, String seccion, String profesor,
                         String color, double zonaTotal, double zonaObtenida, double examenFinal,
                         double notaTotal, String estado, List<ActividadNota> desglose) {
            this.codigoCurso = codigoCurso;
            this.nombreCurso = nombreCurso;
            this.creditos = creditos;
            this.seccion = seccion;
            this.profesor = profesor;
            this.color = color;
            this.zonaTotal = zonaTotal;
            this.zonaObtenida = zonaObtenida;
            this.examenFinal = examenFinal;
            this.notaTotal = notaTotal;
            this.estado = estado;
            this.desglose = desglose;
        }
    }

    public record CursoHistorial(String codigo, String nombre, int creditos, double notaFinal, String estado, String periodo) {}
    public record SemestreHistorial(String periodo, double promedioSemestre, int creditosSemestre, List<CursoHistorial> cursos) {}

}
