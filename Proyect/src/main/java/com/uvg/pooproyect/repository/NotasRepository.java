package com.uvg.pooproyect.repository;
import com.uvg.pooproyect.dto.RespuestaAcademica.NotaCurso;
import java.util.List;
public interface NotasRepository { List<NotaCurso> obtenerNotas(int carnet); }
