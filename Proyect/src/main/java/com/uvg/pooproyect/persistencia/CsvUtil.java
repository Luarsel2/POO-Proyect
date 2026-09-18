package com.uvg.pooproyect.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistencia de datos elegida para el proyecto: archivos de texto en
 * formato CSV, uno por cada entidad, guardados en la carpeta "data/" en la
 * raíz del proyecto.
 *
 * Por qué CSV y no una base de datos:
 * - Es un prototipo académico de un solo usuario/servidor, sin necesidad de
 *   consultas concurrentes ni relaciones complejas a nivel de motor de BD.
 * - No requiere instalar ni levantar un servidor de base de datos aparte
 *   (MySQL, Postgres, etc.), lo cual simplifica que cualquier compañero o el
 *   profesor pueda correr el proyecto solo con Java y Maven.
 * - Los datos deben sobrevivir a reinicios del servidor (eso es lo que pedía
 *   la investigación de persistencia), y un archivo de texto plano cumple
 *   ese único requisito sin agregar dependencias nuevas al pom.xml.
 * - Al ser pocas entidades y pocos registros (prototipo, no producción), no
 *   se necesita indexado ni transacciones: leer/escribir el archivo completo
 *   en cada cambio es suficiente y más fácil de depurar (se puede abrir el
 *   .csv y ver los datos a simple vista).
 *
 * Limitación conocida (aceptable para este alcance): los valores de texto no
 * deben contener comas, porque el formato usado es CSV simple sin comillas.
 * Por eso escapar() reemplaza cualquier coma por punto y coma antes de
 * guardar.
 */
public class CsvUtil {

    private static final Path CARPETA_DATOS = Paths.get("data");

    public static Path rutaArchivo(String nombreArchivo) {
        return CARPETA_DATOS.resolve(nombreArchivo);
    }

    // Lee un archivo CSV y devuelve cada línea ya separada en columnas.
    // Si el archivo todavía no existe (primera vez que corre el sistema),
    // devuelve una lista vacía en vez de fallar.
    public static List<String[]> leerFilas(String nombreArchivo) {
        List<String[]> filas = new ArrayList<>();
        Path ruta = rutaArchivo(nombreArchivo);
        if (!Files.exists(ruta)) {
            return filas;
        }
        try {
            List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            for (String linea : lineas) {
                if (linea.isBlank()) continue;
                filas.add(linea.split(",", -1));
            }
        } catch (IOException e) {
            System.out.println("No se pudo leer " + nombreArchivo + ": " + e.getMessage());
        }
        return filas;
    }

    // Sobreescribe el archivo completo con las filas actuales. Es la forma
    // más simple de mantener el archivo sincronizado con la lista en
    // memoria del controlador, sin tener que llevar control de qué línea
    // corresponde a qué registro.
    public static void escribirFilas(String nombreArchivo, List<String[]> filas) {
        try {
            Files.createDirectories(CARPETA_DATOS);
            List<String> lineas = new ArrayList<>();
            for (String[] fila : filas) {
                lineas.add(String.join(",", fila));
            }
            Files.write(rutaArchivo(nombreArchivo), lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("No se pudo guardar " + nombreArchivo + ": " + e.getMessage());
        }
    }

    // Evita que un valor con comas rompa el formato de columnas.
    public static String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace(",", ";");
    }

    // Convierte "" a null y deja cualquier otro texto tal cual al leerlo de vuelta.
    public static String leerTexto(String valor) {
        return (valor == null || valor.isEmpty()) ? "" : valor;
    }

    public static int leerEntero(String valor, int porDefecto) {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return porDefecto;
        }
    }

    public static double leerDecimal(String valor, double porDefecto) {
        try {
            return Double.parseDouble(valor);
        } catch (Exception e) {
            return porDefecto;
        }
    }
}
