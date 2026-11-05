package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.Administracion;
import java.util.ArrayList;
import java.util.List;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CsvAdministracionRepository implements AdministracionRepository {
    private static final String ARCHIVO = "administracion.csv";
    private final List<Administracion> listaAdministrativos = new ArrayList<>();


    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            Administracion a = new Administracion(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], col[3], col[4],
                    CsvUtil.leerEntero(col[5], 0), col[6], CsvUtil.leerEntero(col[7], 0),
                    col[8], col[9], col[10]
            );
            listaAdministrativos.add(a);
        }
    }

    @Override
    public void sincronizar() {
        List<String[]> filas = new ArrayList<>();
        for (Administracion a : listaAdministrativos) {
            filas.add(new String[]{
                    String.valueOf(a.getId()), CsvUtil.escapar(a.getNombre()), CsvUtil.escapar(a.getApellido()),
                    CsvUtil.escapar(a.getFechaNacimiento()), CsvUtil.escapar(a.getCorreo()),
                    String.valueOf(a.getTelefono()), CsvUtil.escapar(a.getDireccion()),
                    String.valueOf(a.getCodigoDeEmpleado()), CsvUtil.escapar(a.getFechaDeContratacion()),
                    CsvUtil.escapar(a.getPuesto()), CsvUtil.escapar(a.getDepartamento())
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @Override
    public List<Administracion> obtenerTodos() { return List.copyOf(listaAdministrativos); }

    @Override
    public void agregar(Administracion entidad) { listaAdministrativos.add(entidad); }

    @Override
    public Administracion buscarPorCodigo(int codigo) {
        for (Administracion a : listaAdministrativos) {
            if (a.getCodigoDeEmpleado() == codigo) {
                return a;
            }
        }
        return null;
    }
}
