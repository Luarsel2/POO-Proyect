package com.uvg.pooproyect;
import com.uvg.pooproyect.persistencia.CsvUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.io.UncheckedIOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PersistenciaTest {
    @TempDir Path carpeta;
    @Test void asignacionesRestauranCuposAlReiniciarSinDuplicarlos() throws Exception {
        String anterior = System.getProperty("portal.data-dir");
        try {
            System.setProperty("portal.data-dir", carpeta.toString());
            var catalogo = new com.uvg.pooproyect.repository.CatalogoSeccionesDemo();
            int base = catalogo.buscarSeccionCatalogo(101).getCupoActual();
            var repo = new com.uvg.pooproyect.repository.CsvAsignacionRepository(catalogo);
            repo.guardarAsignaciones(42, List.of(101));
            var otroCatalogo = new com.uvg.pooproyect.repository.CatalogoSeccionesDemo();
            var recargado = new com.uvg.pooproyect.repository.CsvAsignacionRepository(otroCatalogo);
            recargado.cargar();
            assertEquals(List.of(101), recargado.obtenerAsignacionesPorCarnet(42));
            assertEquals(base + 1, otroCatalogo.buscarSeccionCatalogo(101).getCupoActual());
            recargado.cargar();
            assertEquals(base + 1, otroCatalogo.buscarSeccionCatalogo(101).getCupoActual());
        } finally {
            if (anterior == null) System.clearProperty("portal.data-dir"); else System.setProperty("portal.data-dir", anterior);
        }
    }
    @Test void escrituraCompletaYErroresExplicitos() throws Exception {
        Path archivo = carpeta.resolve("prueba.csv");
        CsvUtil.escribirFilas(archivo.toString(), List.<String[]>of(new String[]{"uno", "dos"}));
        assertEquals("uno,dos", Files.readString(archivo).trim());
        CsvUtil.escribirFilas(archivo.toString(), List.<String[]>of(new String[]{"tres", "cuatro"}));
        assertEquals(1, CsvUtil.leerFilas(archivo.toString()).size());
        Path directorio = carpeta.resolve("directorio.csv"); Files.createDirectory(directorio);
        Files.writeString(directorio.resolve("conservar.txt"), "original");
        assertThrows(UncheckedIOException.class, () -> CsvUtil.escribirFilas(directorio.toString(), List.of()));
        assertEquals("original", Files.readString(directorio.resolve("conservar.txt")));
        try (var archivos = Files.list(carpeta)) {
            assertFalse(archivos.anyMatch(p -> p.getFileName().toString().startsWith(".portal-")));
        }
    }
}
