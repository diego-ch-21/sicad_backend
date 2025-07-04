package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICursoRepo extends IGenericRepo<Curso, Integer> {
    boolean existsByCodigo(String codigo);

    @Query("""
    SELECT DISTINCT c 
    FROM Curso c 
    LEFT JOIN FETCH c.cursoHorario 
    WHERE c.cicloAcademico.idCicloAcademico = :idCicloAcademico 
      AND c.enabled = true
    """)
    List<Curso> buscarPorPeriodoAcademico(@Param("idCicloAcademico") Integer idCicloAcademico);

    @Query("SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.cursoHorario")
    List<Curso> findAllWithHorarios();

}
