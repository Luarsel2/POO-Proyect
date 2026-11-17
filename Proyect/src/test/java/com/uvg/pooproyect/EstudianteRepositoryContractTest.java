package com.uvg.pooproyect;

import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.repository.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class EstudianteRepositoryContractTest {
    @TempDir Path datos;

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void implementacionesRespetanElMismoContrato(boolean usarCsv) {
        String anterior = System.getProperty("portal.data-dir");
        System.setProperty("portal.data-dir", datos.toString());
        try {
            EstudianteRepository repository = usarCsv ? new CsvEstudianteRepository() : new Memoria();
            assertNull(repository.buscarPorCarnet(999));
            assertTrue(repository.obtenerTodos().isEmpty(), "Consultar no debe crear estudiantes");
            Estudiante estudiante = new Estudiante(1, "Ana", "Prueba", "2005-01-01", "ana@uvg.edu.gt",
                12345678, "Guatemala", 42, 2026, "Activo", 85, null, null, 42);
            assertSame(estudiante, repository.guardar(estudiante));
            assertSame(estudiante, repository.buscarPorCarnet(42));
            var snapshot = repository.obtenerTodos();
            assertThrows(UnsupportedOperationException.class, snapshot::clear);
            estudiante.setDireccion("Nueva dirección");
            repository.sincronizarEstudiantes();
            if (usarCsv) {
                var recargado = new CsvEstudianteRepository(); recargado.cargar();
                assertEquals("Nueva dirección", recargado.buscarPorCarnet(42).getDireccion());
            }
            assertEquals(1, repository.obtenerTodos().size());
            assertNull(repository.buscarPorCarnet(999));
        } finally {
            if (anterior == null) System.clearProperty("portal.data-dir");
            else System.setProperty("portal.data-dir", anterior);
        }
    }

    private static class Memoria implements EstudianteRepository {
        private final List<Estudiante> estudiantes = new ArrayList<>();
        public List<Estudiante> obtenerTodos() { return List.copyOf(estudiantes); }
        public Estudiante buscarPorCarnet(int carnet) {
            return estudiantes.stream().filter(e -> e.getCarnet() == carnet).findFirst().orElse(null);
        }
        public Estudiante guardar(Estudiante estudiante) { estudiantes.add(estudiante); return estudiante; }
        public void sincronizarEstudiantes() { }
    }
}
