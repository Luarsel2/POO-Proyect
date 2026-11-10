package com.uvg.pooproyect.service.reglas;

import com.uvg.pooproyect.model.SeccionCatalogo;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
@Order(20)
public class CursoUnicoRegla implements ReglaAsignacion {
    @Override
    public Optional<String> validar(List<SeccionCatalogo> anteriores, List<SeccionCatalogo> propuestas) {
        Set<String> cursos = new HashSet<>();
        for (SeccionCatalogo seccion : propuestas) {
            if (!cursos.add(seccion.codigoCurso.toUpperCase(Locale.ROOT))) {
                return Optional.of("Solo puedes asignarte una sección de cada curso.");
            }
        }
        return Optional.empty();
    }
}
