package com.uvg.pooproyect.auth;
import com.uvg.pooproyect.dto.CuentaRequests.*;
import com.uvg.pooproyect.service.CuentaService;
import com.uvg.pooproyect.service.ResultadoCuenta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {
    private final CuentaService service;
    public CuentaController(CuentaService service) { this.service = service; }
    @GetMapping
    public List<Map<String, Object>> listar() { return service.listar(); }
    @PostMapping
    public ResponseEntity<Map<String, String>> crear(@RequestBody CuentaRequest req) { return respuesta(service.crear(req)); }
    @PutMapping("/{carnet}")
    public ResponseEntity<Map<String, String>> cambiarClave(@PathVariable("carnet") int carnet, @RequestBody ClaveRequest req) {
        return respuesta(service.cambiarClave(carnet, req));
    }
    @DeleteMapping("/{carnet}")
    public ResponseEntity<Map<String, String>> eliminar(@PathVariable("carnet") int carnet) { return respuesta(service.eliminar(carnet)); }
    private ResponseEntity<Map<String, String>> respuesta(ResultadoCuenta resultado) {
        HttpStatus status = switch (resultado.estado()) {
            case OK -> HttpStatus.OK;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
        };
        return ResponseEntity.status(status).body(Map.of("mensaje", resultado.mensaje()));
    }
}
