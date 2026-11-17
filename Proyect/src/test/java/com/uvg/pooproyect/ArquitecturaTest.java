package com.uvg.pooproyect;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/** Impide reintroducir dependencias hacia HTTP en servicios o persistencia en controladores. */
class ArquitecturaTest {
    private final Path base = Path.of("src/main/java/com/uvg/pooproyect");
    @Test void serviciosIndependientesDelTransporteYDelCsv() throws IOException {
        revisar("service", "import jakarta.servlet", "import org.springframework.http", "import org.springframework.web",
            "import com.uvg.pooproyect.controller", "CsvUtil", "new Csv");
    }
    @Test void controladoresSinPersistenciaNiOtrosControladores() throws IOException {
        revisar("controller", "CsvUtil", "import com.uvg.pooproyect.repository", "import java.nio.file", "Controller controller");
        revisar("auth", "import com.uvg.pooproyect.controller", "CsvUtil");
    }
    @Test void repositoriosSinDependenciasHaciaServicios() throws IOException {
        revisar("repository", "import com.uvg.pooproyect.service", "import com.uvg.pooproyect.controller");
    }
    private void revisar(String carpeta, String... prohibidos) throws IOException {
        try (var archivos = Files.walk(base.resolve(carpeta))) {
            for (Path archivo : archivos.filter(p -> p.toString().endsWith(".java")).toList()) {
                String codigo = Files.readString(archivo);
                for (String prohibido : prohibidos) assertFalse(codigo.contains(prohibido), archivo + " depende de " + prohibido);
            }
        }
    }
}
