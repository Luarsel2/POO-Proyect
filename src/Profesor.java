public class Profesor extends Empleado {
    private String especialidad;
    private String materias;

    public Profesor(int id, String nombre, String apellido, String fechaNacimiento, String correo,int telefono, String direccion, int codigoDeEmpleado, String fechaDeContratacion, String especialidad, String materias){
        super(id, nombre, apellido, fechaNacimiento, correo, telefono, direccion, codigoDeEmpleado, fechaDeContratacion);
        
        this.especialidad = especialidad;
        this.materias = materias;
    }

    public void asignarNota(){
        System.out.println("Asignando nota...");
    }
    public void consultarCursos(){
        System.out.println("Consultando cursos...");
    }

    public String getEspecialidad(){
        return especialidad;
    }
    public void setEspecialidad(String especialidad){
        this.especialidad = especialidad;
    }

    public String getMaterias(){
        return materias;
    }
    public void setMaterias(String materias){
        this.materias = materias;
    }

}
