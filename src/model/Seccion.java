package model;
import java.util.ArrayList;
import java.util.List;


public class Seccion {
    private int idSeccion;
    private int codigo;
    private int cupoMaximo;
    private int cupoActual;
    private String estado;
    private Profesor profesor;  // Relacion con la clase Profesor
    private DetalleInscripcion detalleInscripcion; // Relacion con la clase DetalleInscripcion
    private List<Horario> horarios;

    public Seccion(int idSeccion, int codigo, int cupoMaximo, int cupoActual, String estado, Profesor profesor, DetalleInscripcion detalleInscripcion){
        this.idSeccion = idSeccion;
        this.codigo = codigo;
        this.cupoMaximo = cupoMaximo;
        this.cupoActual = cupoActual;
        this.estado = estado;
        this.profesor = profesor;
        this.detalleInscripcion = detalleInscripcion;
        this.horarios = new ArrayList<>();
    }
    // verifica si aun hay espacios disponibles
    public boolean verificarCupos(){
        return this.cupoActual < this.cupoMaximo;
    }
    // contador de estudiantes que va incrementando media vez halla espacio
    public void agregarEstudiante(){
        if (verificarCupos()){
            this.cupoActual++;
            System.out.println("Estudiante agregado a la seccion.");
        } else {
            System.out.println("Falto de cupos disponibles en esta seccion.");
        }
    }
    // decrementa cuando se retiraran estudiantes
    public void retirarEstudiante(){
        if (this.cupoActual > 0){
            this.cupoActual--;
            System.out.println("Estudiante retirado de la seccion.");
        } else {
            System.out.println("La seccion no tiene estudiantes registrados.");
        }
    }
    // calculo de cuantos cupos quedan libres
    public int obtenerCuposDisponibles(){
        return this.cupoMaximo - this.cupoActual;
    }

    public void agregarHorario(int idHorario, String diaSemana, String horaInicio, String horaFin){
        Horario horario = new Horario(idHorario, diaSemana, horaInicio, horaFin);
        this.horarios.add(horario);
    }

    public int getIdSeccion(){
        return idSeccion;
    }
    public void setIdSeccion(int idSeccion){
        this.idSeccion = idSeccion;
    }

    public int getCodigo(){
        return codigo;
    }
    public void setCodigo(int codigo){
        this.codigo = codigo;
    }

    public int getCupoMaximo(){
        return cupoMaximo;
    }
    public void setCupoMaximo(int cupoMaximo){
        this.cupoMaximo = cupoMaximo;
    }

    public int getCupoActual(){
        return cupoActual;
    }
    public void setCupoActual(int cupoActual){
        this.cupoActual = cupoActual;
    }

    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }

    public Profesor getProfesor(){
        return profesor;
    }
    public void setProfesor(Profesor profesor){
        this.profesor = profesor;
    }

    public DetalleInscripcion getDetalleInscripcion(){
        return detalleInscripcion;
    }
    public void setDetalleInscripcion(DetalleInscripcion detalleInscripcion){
        this.detalleInscripcion = detalleInscripcion;
    }

    public List<Horario> getHorario(){
        return horarios;
    }
    public void setHorario(List<Horario> horarios){
        this.horarios = horarios;
    }
}
