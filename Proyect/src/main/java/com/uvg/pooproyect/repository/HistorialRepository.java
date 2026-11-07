package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.dto.RespuestaAcademica.SemestreHistorial;
import java.util.List;
public interface HistorialRepository { List<SemestreHistorial> obtenerSemestres(int carnet); }
