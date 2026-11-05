package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.model.SeccionCatalogo;
import java.util.List;

public interface CatalogoSeccionRepository {
    List<SeccionCatalogo> obtenerCatalogoSecciones();
    SeccionCatalogo buscarSeccionCatalogo(int idSeccion);
}
