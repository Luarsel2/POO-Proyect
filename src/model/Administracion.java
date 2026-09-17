package model;
public class Administracion extends Empleado{
    private String puesto;
    private String departamento;

    public Administracion(int id, String nombre, String apellido, String fechaNacimiento, String correo, int telefono, String direccion, int codigoDeEmpleado, String fechaDeContratacion, String puesto, String departamento){
        super(id, nombre, apellido, fechaNacimiento, correo, telefono, direccion, codigoDeEmpleado, fechaDeContratacion);
        this.puesto = puesto;
        this.departamento = departamento;
    }

    public String getPuesto(){
        return puesto;
    }
    public void setPuesto(String puesto){
        this.puesto = puesto;
    }

    public String getDepartamento(){
        return departamento;
    }
    public void setDepartamento(String departamento){
        this.departamento = departamento;
    }
}
