package com.uvg.pooproyect.service.reglas;

import com.uvg.pooproyect.model.SeccionCatalogo;
import java.util.List;
import java.util.Optional;

/** Una regla consulta la selección propuesta sin modificar estudiantes, cupos ni archivos. */
public interface ReglaAsignacion {
    Optional<String> validar(List<SeccionCatalogo> anteriores, List<SeccionCatalogo> propuestas);
}
