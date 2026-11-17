package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class CsvAsignacionRepository implements AsignacionRepository {
    private static final String ARCHIVO_ASIGNACIONES = "asignaciones.csv";
    private final Map<Integer, List<Integer>> asignacionesPorCarnet = new LinkedHashMap<>();
    private final CatalogoSeccionRepository catalogo;
    public CsvAsignacionRepository(CatalogoSeccionRepository catalogo) { this.catalogo = catalogo; }

    @PostConstruct
    public void cargar() {
        asignacionesPorCarnet.clear();
        List<String[]> filas = CsvUtil.leerFilas(ARCHIVO_ASIGNACIONES);
        for (String[] col : filas) {
            if (col.length >= 2) {
                int carnet = CsvUtil.leerEntero(col[0], 0);
                int idSec = CsvUtil.leerEntero(col[1], 0);
                if (carnet > 0 && idSec > 0 && catalogo.buscarSeccionCatalogo(idSec) != null) {
                    asignacionesPorCarnet.computeIfAbsent(carnet, k -> new ArrayList<>()).add(idSec);
                }
            }
        }
        Map<Integer, Integer> inscritos = new HashMap<>();
        for (List<Integer> seleccion : asignacionesPorCarnet.values()) {
            for (Integer id : seleccion) inscritos.merge(id, 1, Integer::sum);
        }
        for (var seccion : catalogo.obtenerCatalogoSecciones()) {
            seccion.restaurarAsignaciones(inscritos.getOrDefault(seccion.idSeccion, 0));
        }
    }
    private void guardarAsignacionesEnCsv() {
        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<Integer, List<Integer>> entry : asignacionesPorCarnet.entrySet()) {
            for (Integer idSec : entry.getValue()) {
                filas.add(new String[]{String.valueOf(entry.getKey()), String.valueOf(idSec)});
            }
        }
        CsvUtil.escribirFilas(ARCHIVO_ASIGNACIONES, filas);
    }
    @Override
    public List<Integer> obtenerAsignacionesPorCarnet(int carnet) {
        return List.copyOf(asignacionesPorCarnet.getOrDefault(carnet, List.of()));
    }

    @Override
    public void guardarAsignaciones(int carnet, List<Integer> asignaciones) {
        var anteriores = asignacionesPorCarnet.put(carnet, List.copyOf(asignaciones));
        try { guardarAsignacionesEnCsv(); }
        catch (RuntimeException error) {
            if (anteriores == null) asignacionesPorCarnet.remove(carnet);
            else asignacionesPorCarnet.put(carnet, anteriores);
            throw error;
        }
    }
}
