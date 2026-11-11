package com.uvg.pooproyect.service;
import com.uvg.pooproyect.auth.*;
import com.uvg.pooproyect.repository.*;
import com.uvg.pooproyect.model.Estudiante;
import com.uvg.pooproyect.dto.CuentaRequests.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CuentaService implements ConsultaCuenta, VerificadorCuenta {
    private static final int CLAVE_MINIMA = 6;
    private final EstudianteRepository estudiantes;
    private final CuentaRepository cuentas;
    private final CodificadorClave codificador;
    public CuentaService(EstudianteRepository estudiantes, CuentaRepository cuentas, CodificadorClave codificador) {
        this.estudiantes = estudiantes; this.cuentas = cuentas; this.codificador = codificador;
    }
    @PostConstruct
    public void inicializarCuentas() {
        if (!cuentas.inicializado()) {
            for (Estudiante e : estudiantes.obtenerTodos()) {
                if (e.getCorreo() != null) cuentas.guardar(e.getCarnet(), codificador.cifrar(e.getCarnet(), e.getCorreo().trim()));
            }
        }
    }
    public boolean existe(int carnet) { return cuentas.buscarHash(carnet) != null; }
    public boolean verificar(int carnet, String clave) {
        String hash = cuentas.buscarHash(carnet);
        return hash != null && hash.equals(codificador.cifrar(carnet, clave));
    }

    public List<Map<String, Object>> listar() {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (Integer carnet : cuentas.obtenerTodas().keySet()) {
            Estudiante e = estudiantes.buscarPorCarnet(carnet);
            Map<String, Object> fila = new LinkedHashMap<>();
            fila.put("carnet", carnet);
            fila.put("nombre", e != null ? e.getNombre() : "");
            fila.put("apellido", e != null ? e.getApellido() : "");
            lista.add(fila);
        }
        return lista;
    }

    public ResultadoCuenta crear(CuentaRequest req) {
        if (req.carnet() == null) {
            return new ResultadoCuenta(ResultadoCuenta.Estado.BAD_REQUEST, "Indica el carnet del estudiante.");
        }
        if (estudiantes.buscarPorCarnet(req.carnet()) == null) {
            return new ResultadoCuenta(ResultadoCuenta.Estado.NOT_FOUND, "No existe un estudiante con el carnet " + req.carnet() + ".");
        }
        if (existe(req.carnet())) {
            return new ResultadoCuenta(ResultadoCuenta.Estado.CONFLICT, "El carnet " + req.carnet() + " ya tiene una cuenta.");
        }
        String error = validarClave(req.clave());
        if (error != null) {
            return new ResultadoCuenta(ResultadoCuenta.Estado.BAD_REQUEST, error);
        }
        cuentas.guardar(req.carnet(), codificador.cifrar(req.carnet(), req.clave().trim()));
        return new ResultadoCuenta(ResultadoCuenta.Estado.OK, "Cuenta creada para el carnet " + req.carnet() + ".");
    }

    public ResultadoCuenta cambiarClave(int carnet,
                                                            ClaveRequest req) {
        if (!existe(carnet)) {
            return new ResultadoCuenta(ResultadoCuenta.Estado.NOT_FOUND, "El carnet " + carnet + " no tiene cuenta.");
        }
        String error = validarClave(req.clave());
        if (error != null) {
            return new ResultadoCuenta(ResultadoCuenta.Estado.BAD_REQUEST, error);
        }
        cuentas.guardar(carnet, codificador.cifrar(carnet, req.clave().trim()));
        return new ResultadoCuenta(ResultadoCuenta.Estado.OK, "Contraseña actualizada para el carnet " + carnet + ".");
    }

    public ResultadoCuenta eliminar(int carnet) {
        if (!cuentas.eliminar(carnet)) {
            return new ResultadoCuenta(ResultadoCuenta.Estado.NOT_FOUND, "El carnet " + carnet + " no tiene cuenta.");
        }
        return new ResultadoCuenta(ResultadoCuenta.Estado.OK, "Cuenta eliminada para el carnet " + carnet + ".");
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
}
