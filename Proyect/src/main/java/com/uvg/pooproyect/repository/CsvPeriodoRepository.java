package com.uvg.pooproyect.repository;

import com.uvg.pooproyect.model.PeriodoAcademico;
import java.util.ArrayList;
import java.util.List;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

@Repository
public class CsvPeriodoRepository implements PeriodoRepository {
    private static final String ARCHIVO = "periodos.csv";
    private final List<PeriodoAcademico> listaPeriodos = new ArrayList<>();


    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            listaPeriodos.add(new PeriodoAcademico(
                    CsvUtil.leerEntero(col[0], 0), col[1], col[2], col[3], col[4], col[5], col[6], null
            ));
        }
    }

    @Override
    public void sincronizar() {
        List<String[]> filas = new ArrayList<>();
        for (PeriodoAcademico p : listaPeriodos) {
            filas.add(new String[]{
                    String.valueOf(p.getIdPeriodo()), CsvUtil.escapar(p.getNombre()),
                    CsvUtil.escapar(p.getFechaInicio()), CsvUtil.escapar(p.getFechaFin()),
                    CsvUtil.escapar(p.getFechaInicioInscripcion()), CsvUtil.escapar(p.getFechaFinInscripcion()),
                    CsvUtil.escapar(p.getEstado())
            });
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    @Override
    public List<PeriodoAcademico> obtenerTodos() { return List.copyOf(listaPeriodos); }

    @Override
    public void agregar(PeriodoAcademico entidad) { listaPeriodos.add(entidad); }

    @Override
    public PeriodoAcademico buscarPorId(int idPeriodo) {
        for (PeriodoAcademico p : listaPeriodos) {
            if (p.getIdPeriodo() == idPeriodo) {
                return p;
            }
        }
        return null;
    }
}
