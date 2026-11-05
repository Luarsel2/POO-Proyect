package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class CsvEstudianteRepository implements EstudianteRepository {
    private static final String ARCHIVO = "estudiantes.csv";
    private final List<Estudiante> listaEstudiantes = new ArrayList<>();
    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Estudiante e = new Estudiante(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], col[3], col[4],
                    CsvUtil.leerEntero(col[5], 0), col[6], CsvUtil.leerEntero(col[7], 0),
                    CsvUtil.leerEntero(col[8], 0), col[9], CsvUtil.leerDecimal(col[10], 0),
                    null, null, CsvUtil.leerEntero(col[11], 0)
            );
            listaEstudiantes.add(e);
        }
    }
    private void guardarEnCsv() {
        List<String[]> filas = new ArrayList<>();
        for (Estudiante e : listaEstudiantes) {
            filas.add(new String[]{
                    String.valueOf(e.getId()), CsvUtil.escapar(e.getNombre()), CsvUtil.escapar(e.getApellido()),
                    CsvUtil.escapar(e.getFechaNacimiento()), CsvUtil.escapar(e.getCorreo()),
                    String.valueOf(e.getTelefono()), CsvUtil.escapar(e.getDireccion()),
                    String.valueOf(e.getCarnet()), String.valueOf(e.getFechaIngreso()),
                    CsvUtil.escapar(e.getEstado()), String.valueOf(e.getPromedioGeneral()),
                    String.valueOf(e.getHistorialAcademico().getIdHistorial())
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }
    public Estudiante buscarPorCarnet(int carnet) {
        for (Estudiante e : listaEstudiantes) {
            if (e.getCarnet() == carnet) {
                return e;
            }
        }
        return null;
    }
    public Estudiante guardar(Estudiante estudiante) {
        listaEstudiantes.add(estudiante);
        guardarEnCsv();
        return estudiante;
    }
    public void sincronizarEstudiantes() {
        guardarEnCsv();
    }
    @Override
    public List<Estudiante> obtenerTodos() { return List.copyOf(listaEstudiantes); }
}
