package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Profesor;
import java.util.ArrayList;
import java.util.List;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CsvProfesorRepository implements ProfesorRepository {
    private static final String ARCHIVO = "profesores.csv";
    private final List<Profesor> listaProfesores = new ArrayList<>();


    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Profesor p = new Profesor(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], col[3], col[4],
                    CsvUtil.leerEntero(col[5], 0), col[6], CsvUtil.leerEntero(col[7], 0),
                    col[8], col[9], col[10]
            );
            listaProfesores.add(p);
        }
    }

    @Override
    public void sincronizar() {
        List<String[]> filas = new ArrayList<>();
        for (Profesor p : listaProfesores) {
            filas.add(new String[]{
                    String.valueOf(p.getId()), CsvUtil.escapar(p.getNombre()), CsvUtil.escapar(p.getApellido()),
                    CsvUtil.escapar(p.getFechaNacimiento()), CsvUtil.escapar(p.getCorreo()),
                    String.valueOf(p.getTelefono()), CsvUtil.escapar(p.getDireccion()),
                    String.valueOf(p.getCodigoDeEmpleado()), CsvUtil.escapar(p.getFechaDeContratacion()),
                    CsvUtil.escapar(p.getEspecialidad()), CsvUtil.escapar(p.getMaterias())
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @Override
    public List<Profesor> obtenerTodos() { return List.copyOf(listaProfesores); }

    @Override
    public void agregar(Profesor entidad) { listaProfesores.add(entidad); }

    @Override
    public Profesor buscarPorCodigo(int codigo) {
        for (Profesor p : listaProfesores) {
            if (p.getCodigoDeEmpleado() == codigo) {
                return p;
            }
        }
        return null;
    }
}
