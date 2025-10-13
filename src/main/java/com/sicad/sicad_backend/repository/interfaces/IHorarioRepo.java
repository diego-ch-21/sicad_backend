package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Horario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IHorarioRepo extends IGenericRepo<Horario, Integer>  {

    @Query("SELECT h FROM Horario h WHERE h.enabled = true AND h.curso.idCurso = :idCurso")
    List<Horario> findHorariosByCurso(@Param("idCurso") Integer idCurso);


    @Query("SELECT e FROM Horario e WHERE e.idHorario = :id AND e.enabled = true")
    Optional<Horario> findByIdAndEnabledTrue(@Param("id") Integer idHorario);
}
