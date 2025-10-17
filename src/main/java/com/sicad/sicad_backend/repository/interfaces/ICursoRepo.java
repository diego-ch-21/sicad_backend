package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ICursoRepo extends IGenericRepo<Curso, Integer> {

    @Query("SELECT COUNT(c) > 0 FROM Curso c WHERE c.codigo = :codigo AND c.enabled = true")
    boolean existsByCodigoAndEnabled(@Param("codigo") String codigo);


    @Query("SELECT e FROM Curso e WHERE e.enabled = true")
    List<Curso> findByEnabledTrue();

    @Query("SELECT e FROM Curso e WHERE e.idCurso = :id AND e.enabled = true")
    Optional<Curso> findByIdAndEnabledTrue(@Param("id") Integer idCurso);

    @Query("SELECT c FROM Curso c WHERE c.enabled = true AND c.cicloAcademico.idCicloAcademico = :id")
    List<Curso> findByEnabledTrueAndCicloAcademico(@Param("id") Integer idCicloAcademico);

    @Query("""
    SELECT c FROM Curso c
    WHERE c.enabled = true
      AND (:idAsignatura IS NULL OR c.asignatura.idAsignatura = :idAsignatura)
      AND (:idCicloAcademico IS NULL OR c.cicloAcademico.idCicloAcademico = :idCicloAcademico)
      AND (:idEscuela IS NULL OR c.escuela.idEscuela = :idEscuela)
    """)
    List<Curso> findByAsignaturaCicloAcademicoEscuela(
            @Param("idCicloAcademico") Integer idCicloAcademico,
            @Param("idEscuela") Integer idEscuela,
            @Param("idAsignatura") Integer idAsignatura

    );


    //------------------------------------------------------------------------------------
    /**
     * Busca cursos habilitados de un ciclo académico específico (enabled = true).
     * Usa LEFT JOIN FETCH para traer también la relación cursoHorario en una sola consulta,
     * evitando LazyInitializationException.
     */
    @Query("""
       SELECT DISTINCT c
       FROM Curso c
       LEFT JOIN FETCH c.horario
       WHERE c.cicloAcademico.idCicloAcademico = :idCicloAcademico
         AND c.enabled = true
       """)
    List<Curso> buscarPorCicloAcademico(@Param("idCicloAcademico") Integer idCicloAcademico);


}
