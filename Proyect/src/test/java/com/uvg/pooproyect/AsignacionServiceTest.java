package com.uvg.pooproyect;

import com.uvg.pooproyect.model.*;
import com.uvg.pooproyect.repository.*;
import com.uvg.pooproyect.service.AsignacionService;
import com.uvg.pooproyect.service.reglas.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class AsignacionServiceTest {
    @TempDir Path datos;
    CatalogoSeccionesDemo catalogo = new CatalogoSeccionesDemo();
    MemoriaAsignaciones asignaciones = new MemoriaAsignaciones();
    CsvEstudianteRepository estudiantes;
    AsignacionService service;
    @BeforeEach void preparar() throws Exception {
        Files.writeString(datos.resolve("estudiantes.csv"), "1,Ana,Test,2005-01-01,ana@uvg.edu.gt,12345678,Guatemala,42,2026,Activo,85,42\n");
        String anterior = System.getProperty("portal.data-dir");
        try {
            System.setProperty("portal.data-dir", datos.toString());
            estudiantes = new CsvEstudianteRepository(); estudiantes.cargar();
        } finally {
            if (anterior == null) System.clearProperty("portal.data-dir"); else System.setProperty("portal.data-dir", anterior);
        }
        service = new AsignacionService(estudiantes, asignaciones, catalogo,
            List.of(new CupoDisponibleRegla(), new CursoUnicoRegla()));
    }
    @Test void cambiarASeccionLlenaConservaLaAnterior() {
        assertEquals(true, service.asignarCurso(42, Map.of("idSeccion",101)).get("ok"));
        SeccionCatalogo llena = catalogo.buscarSeccionCatalogo(201);
        while (llena.getCupoActual() < llena.cupoMaximo) llena.reservarCupo();
        int cuposAntes = catalogo.buscarSeccionCatalogo(101).getCupoActual();
        assertEquals(false, service.asignarCurso(42, Map.of("idSeccion",201)).get("ok"));
        assertEquals(List.of(101), asignaciones.ids);
        assertEquals(cuposAntes, catalogo.buscarSeccionCatalogo(101).getCupoActual());
    }
    @Test void horarioCompletoSeValidaAntesDeModificar() {
        service.asignarCurso(42, Map.of("idSeccion",101));
        SeccionCatalogo llena = catalogo.buscarSeccionCatalogo(206);
        while (llena.getCupoActual() < llena.cupoMaximo) llena.reservarCupo();
        assertEquals(false, service.asignarHorarioCompleto(42, Map.of("jornada","Nocturna")).get("ok"));
        assertEquals(List.of(101), asignaciones.ids);
    }
    @Test void permiteAgregarUnaReglaSinModificarElServicio() {
        ReglaAsignacion adicional = (anteriores, propuestas) -> Optional.of("Periodo cerrado");
        var extendido = new AsignacionService(estudiantes, asignaciones, catalogo, List.of(adicional));
        assertEquals("Periodo cerrado", extendido.asignarCurso(42, Map.of("idSeccion",101)).get("mensaje"));
        assertTrue(asignaciones.ids.isEmpty());
    }
    @Test void errorDePersistenciaNoModificaCuposNiSeleccion() {
        int cupos = catalogo.buscarSeccionCatalogo(101).getCupoActual();
        asignaciones.fallar = true;
        assertThrows(IllegalStateException.class, () -> service.asignarCurso(42, Map.of("idSeccion",101)));
        assertEquals(cupos, catalogo.buscarSeccionCatalogo(101).getCupoActual());
        assertTrue(asignaciones.ids.isEmpty());
    }
    @Test void repetirHorarioNoConsumeCuposOtraVez() {
        assertEquals(true, service.asignarHorarioCompleto(42, Map.of("jornada","Matutina")).get("ok"));
        int cupos = catalogo.buscarSeccionCatalogo(101).getCupoActual();
        service.asignarHorarioCompleto(42, Map.of("jornada","Matutina"));
        assertEquals(cupos, catalogo.buscarSeccionCatalogo(101).getCupoActual());
        assertEquals(6, asignaciones.ids.size());
        service.retirarTodosLosCursos(42);
        assertEquals(cupos - 1, catalogo.buscarSeccionCatalogo(101).getCupoActual());
    }
    private static class MemoriaAsignaciones implements AsignacionRepository {
        List<Integer> ids = List.of(); boolean fallar;
        public List<Integer> obtenerAsignacionesPorCarnet(int carnet) { return ids; }
        public void guardarAsignaciones(int carnet, List<Integer> nuevos) {
            if (fallar) throw new IllegalStateException("Sin acceso al archivo");
            ids = List.copyOf(nuevos);
        }
    }
}
