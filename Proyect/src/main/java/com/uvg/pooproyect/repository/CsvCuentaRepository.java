package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.persistencia.CsvUtil;
import org.springframework.stereotype.Repository;
import jakarta.annotation.PostConstruct;
import java.nio.file.Files;
import java.util.*;

@Repository
public class CsvCuentaRepository implements CuentaRepository {
    private static final String ARCHIVO = "cuentas.csv";
    private final Map<Integer, String> cuentas = new LinkedHashMap<>();
    public boolean inicializado() { return Files.exists(CsvUtil.rutaArchivo(ARCHIVO)); }
    @PostConstruct
    public void cargar() {
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            cuentas.put(CsvUtil.leerEntero(col[0], 0), col[1]);
        }
    }
    public Map<Integer, String> obtenerTodas() { return Collections.unmodifiableMap(new LinkedHashMap<>(cuentas)); }
    public String buscarHash(int carnet) { return cuentas.get(carnet); }
    public void guardar(int carnet, String hash) {
        Map<Integer, String> nuevas = new LinkedHashMap<>(cuentas);
        nuevas.put(carnet, hash);
        persistir(nuevas);
    }
    public boolean eliminar(int carnet) {
        if (!cuentas.containsKey(carnet)) return false;
        Map<Integer, String> nuevas = new LinkedHashMap<>(cuentas);
        nuevas.remove(carnet);
        persistir(nuevas);
        return true;
    }
    private void persistir(Map<Integer, String> nuevas) {
        List<String[]> filas = nuevas.entrySet().stream()
            .map(e -> new String[]{String.valueOf(e.getKey()), e.getValue()}).toList();
        CsvUtil.escribirFilas(ARCHIVO, filas);
        cuentas.clear(); cuentas.putAll(nuevas);
    }
}
