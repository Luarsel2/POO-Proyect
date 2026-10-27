package com.uvg.pooproyect.auth;

import com.uvg.pooproyect.controller.EstudianteController;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.persistencia.CsvUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Cuentas de acceso de los estudiantes. Solo el administrador usa estos
// endpoints (AuthFilter bloquea /api/cuentas a los estudiantes).
// Se guardan en data/cuentas.csv como: carnet,contraseña_cifrada
// La contraseña nunca se guarda en texto plano.
@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private static final String ARCHIVO = "cuentas.csv";
    private static final int CLAVE_MINIMA = 6;

    public record CuentaRequest(Integer carnet, String clave) {}
    public record ClaveRequest(String clave) {}

    private final EstudianteController estudianteController;
    private final Map<Integer, String> cuentas = new LinkedHashMap<>();

    public CuentaController(EstudianteController estudianteController) {
        this.estudianteController = estudianteController;
    }

    @PostConstruct
    public void cargar() {
        // Primera vez que corre el sistema: los estudiantes que ya existen
        // conservan su acceso, con su correo como contraseña inicial.
        if (!Files.exists(CsvUtil.rutaArchivo(ARCHIVO))) {
            for (Estudiante e : estudianteController.obtenerTodos()) {
                cuentas.put(e.getCarnet(), cifrar(e.getCarnet(), e.getCorreo().trim()));
            }
            guardar();
            return;
        }
        for (String[] col : CsvUtil.leerFilas(ARCHIVO)) {
            cuentas.put(CsvUtil.leerEntero(col[0], 0), col[1]);
        }
    }

    private void guardar() {
        List<String[]> filas = new ArrayList<>();
        for (Map.Entry<Integer, String> c : cuentas.entrySet()) {
            filas.add(new String[]{String.valueOf(c.getKey()), c.getValue()});
        }
        CsvUtil.escribirFilas(ARCHIVO, filas);
    }

    // Lo usa AuthController al iniciar sesión.
    public boolean verificar(int carnet, String clave) {
        String guardada = cuentas.get(carnet);
        return guardada != null && guardada.equals(cifrar(carnet, clave));
    }

    // Lo usa AuthFilter para cortar la sesión si la cuenta fue eliminada.
    public boolean existe(int carnet) {
        return cuentas.containsKey(carnet);
    }

    @GetMapping
    public List<Map<String, Object>> listar() {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Integer carnet : cuentas.keySet()) {
            Estudiante e = estudianteController.buscarPorCarnet(carnet);
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("carnet", carnet);
            fila.put("nombre", e != null ? e.getNombre() : "");
            fila.put("apellido", e != null ? e.getApellido() : "");
            lista.add(fila);
        }
        return lista;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@RequestBody CuentaRequest req) {
        if (req.carnet() == null) {
            return respuesta(HttpStatus.BAD_REQUEST, "Indica el carnet del estudiante.");
        }
        if (estudianteController.buscarPorCarnet(req.carnet()) == null) {
            return respuesta(HttpStatus.NOT_FOUND, "No existe un estudiante con el carnet " + req.carnet() + ".");
        }
        if (cuentas.containsKey(req.carnet())) {
            return respuesta(HttpStatus.CONFLICT, "El carnet " + req.carnet() + " ya tiene una cuenta.");
        }
        String error = validarClave(req.clave());
        if (error != null) {
            return respuesta(HttpStatus.BAD_REQUEST, error);
        }
        cuentas.put(req.carnet(), cifrar(req.carnet(), req.clave().trim()));
        guardar();
        return respuesta(HttpStatus.OK, "Cuenta creada para el carnet " + req.carnet() + ".");
    }

    @PutMapping("/{carnet}")
    public ResponseEntity<Map<String, String>> cambiarClave(@PathVariable("carnet") int carnet,
                                                            @RequestBody ClaveRequest req) {
        if (!cuentas.containsKey(carnet)) {
            return respuesta(HttpStatus.NOT_FOUND, "El carnet " + carnet + " no tiene cuenta.");
        }
        String error = validarClave(req.clave());
        if (error != null) {
            return respuesta(HttpStatus.BAD_REQUEST, error);
        }
        cuentas.put(carnet, cifrar(carnet, req.clave().trim()));
        guardar();
        return respuesta(HttpStatus.OK, "Contraseña actualizada para el carnet " + carnet + ".");
    }

    // Elimina solo la cuenta de acceso; el registro del estudiante se conserva.
    @DeleteMapping("/{carnet}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("carnet") int carnet) {
        if (cuentas.remove(carnet) == null) {
            return respuesta(HttpStatus.NOT_FOUND, "El carnet " + carnet + " no tiene cuenta.");
        }
        guardar();
        return respuesta(HttpStatus.OK, "Cuenta eliminada para el carnet " + carnet + ".");
    }

    private String validarClave(String clave) {
        if (clave == null || clave.trim().length() < CLAVE_MINIMA) {
            return "La contraseña debe tener al menos " + CLAVE_MINIMA + " caracteres.";
        }
        if (clave.contains(",")) {
            return "La contraseña no puede llevar comas.";
        }
        return null;
    }

    private ResponseEntity<Map<String, String>> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(Map.of("mensaje", mensaje));
    }

    // SHA-256 del carnet junto con la contraseña.
    private String cifrar(int carnet, String clave) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest((carnet + ":" + clave).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
