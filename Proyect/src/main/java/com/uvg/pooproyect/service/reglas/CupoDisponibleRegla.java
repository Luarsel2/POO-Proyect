package com.uvg.pooproyect.service.reglas;

import com.uvg.pooproyect.model.SeccionCatalogo;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
@Order(10)
public class CupoDisponibleRegla implements ReglaAsignacion {
    @Override
    public Optional<String> validar(List<SeccionCatalogo> anteriores, List<SeccionCatalogo> propuestas) {
        for (SeccionCatalogo propuesta : propuestas) {
            boolean yaAsignada = anteriores.stream().anyMatch(s -> s.idSeccion == propuesta.idSeccion);
            if (!yaAsignada && propuesta.getCupoActual() >= propuesta.cupoMaximo) {
                return Optional.of("No hay cupos disponibles en esta sección.");
            }
        }
        return Optional.empty();
    }
}
