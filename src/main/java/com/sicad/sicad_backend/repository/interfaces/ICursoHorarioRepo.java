package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.model.CursoHorario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICursoHorarioRepo extends IGenericRepo<CursoHorario, Integer>  {


    @Query("SELECT ch FROM CursoHorario ch WHERE ch.curso.idCurso = :idCurso AND ch.enabled = true")
    List<CursoHorario> listarPorCursoHabilitado(@Param("idCurso") Integer idCurso);
}
