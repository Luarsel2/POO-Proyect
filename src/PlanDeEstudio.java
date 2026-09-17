import java.util.ArrayList;
import java.util.List;

public class PlanDeEstudio {
    private int year;
    private String estado;
    //  Agregacion
    private List<Curso> cursos;

    public PlanDeEstudio(int year, String estado){
        this.year = year;
        this.estado = estado;
        this.cursos = new ArrayList<>();
    }

    public void agregarCurso(Curso curso){
        if (curso != null){
            this.cursos.add(curso);
        }
    }

    public int getYear(){
        return year;
    }
    public void setYear(int year){
        this.year = year;
    }

    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }

    public List<Curso> getCursos(){
        return cursos;
    }
}
