public class Estudiante extends Persona {
    // atributos
    private int carnet;
    private int fechaIngreso;
    private String estado;
    private double promedioGeneral;
    private Inscripcion inscripcion;
    private Curso curso;
    private HistorialAcademico historialAcademico;
    // constructor
    public Estudiante(int id, String nombre, String apellido, String fechaNacimiento, String correo, int telefono, String direccion, int carnet, int fechaIngreso, String estado, double promedioGeneral, Inscripcion inscripcion, Curso curso, int idHistorial){
        super(id, nombre, apellido, fechaNacimiento, correo, telefono, direccion);
        this.carnet = carnet;
        this.fechaIngreso = fechaIngreso;
        this.estado = estado;
        this.promedioGeneral = promedioGeneral;
        this.inscripcion = inscripcion;
        this.curso = curso;
        this.historialAcademico = new HistorialAcademico(idHistorial);
    }
    
    //  Metodos
    public void inscribirCurso(){
        System.out.println("Inscribiendo curso...");
    }

    public void retirarCurso(){
        System.out.println("Retirando curso...");
    }

    public void consultarNota(){
        System.out.println("Consultando nota... ");
    }

    public void consultarHorario(){
        System.out.println("Consultando horario... ");
    }
    //  getter
    public int getCarnet(){
        return carnet;
    }
    //  setter
    public void setCarnet(int carnet){
        this.carnet = carnet;
    }

    public int getFechaIngreso(){
        return fechaIngreso;
    }
    public void setFechaIngreso(int fechaIngreso){
        this.fechaIngreso = fechaIngreso;
    }

    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }

    public double getPromedioGeneral(){
        return promedioGeneral;
    }
    public void setPromedioGeneral(double promedioGeneral){
        this.promedioGeneral = promedioGeneral;
    }

    public Inscripcion getInscripcion(){
        return inscripcion;
    }
    public void setInscripcion(Inscripcion inscripcion){
        this.inscripcion = inscripcion;
    }

    public Curso getCurso(){
        return curso;
    }
    public void setCurso(Curso curso){
        this.curso = curso;
    }

    public HistorialAcademico getHistorialAcademico(){
        return historialAcademico;
    }
    public void setHistorialAcademico(HistorialAcademico historialAcademico){
        this.historialAcademico = historialAcademico;
    }
}
