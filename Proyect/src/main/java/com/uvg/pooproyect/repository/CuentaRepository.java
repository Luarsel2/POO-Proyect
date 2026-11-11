package com.uvg.pooproyect.repository;
import java.util.Map;
public interface CuentaRepository {
    boolean inicializado();
    Map<Integer, String> obtenerTodas();
    String buscarHash(int carnet);
    void guardar(int carnet, String hash);
    boolean eliminar(int carnet);
}
