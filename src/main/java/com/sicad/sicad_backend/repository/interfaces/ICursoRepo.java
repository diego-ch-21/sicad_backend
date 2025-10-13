package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICursoRepo extends IGenericRepo<Curso, Integer> {

    // Verifica si ya existe un curso con el código proporcionado (para evitar duplicados).
    boolean existsByCodigo(String codigo);


    @Query("SELECT e FROM Curso e WHERE e.enabled = true")
    List<Curso> findByEnabledTrue();

    @Query("SELECT e FROM Curso e WHERE e.idCurso = :id AND e.enabled = true")
    Optional<Curso> findByIdAndEnabledTrue(@Param("id") Integer idCurso);

    @Query("SELECT c FROM Curso c WHERE c.enabled = true AND c.cicloAcademico.idCicloAcademico = :id")
    List<Curso> findByEnabledTrueAndCicloAcademico(@Param("id") Integer idCicloAcademico);

    /**
     * Busca cursos habilitados de un ciclo académico específico (enabled = true).
     * Usa LEFT JOIN FETCH para traer también la relación cursoHorario en una sola consulta,
     * evitando LazyInitializationException.
     */
    @Query("""
       SELECT DISTINCT c
       FROM Curso c
       LEFT JOIN FETCH c.cursoHorario
       WHERE c.cicloAcademico.idCicloAcademico = :idCicloAcademico
         AND c.enabled = true
       """)
    List<Curso> buscarPorPeriodoAcademico(@Param("idCicloAcademico") Integer idCicloAcademico);


    /**
     * Busca cursos habilitados de un ciclo académico específico y de una carga específica.
     * Además, usa LEFT JOIN FETCH para traer la relación cursoHorario y evitar LazyInitializationException.
     */
    @Query("""
    SELECT DISTINCT c
    FROM Curso c
    LEFT JOIN FETCH c.cursoHorario
    WHERE c.cicloAcademico.idCicloAcademico = :idCicloAcademico
      AND c.enabled = true
    """)
    List<Curso> buscarPorCicloAcademico(
            @Param("idCicloAcademico") Integer idCicloAcademico);



    /**
     * Elimina todos los cursos asociados a un ciclo académico específico.
     * Devuelve el número de registros eliminados.
     * Usa @Modifying porque se trata de una operación de escritura.
     * Usa @Transactional porque modifica datos en la base.
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Curso c WHERE c.cicloAcademico.idCicloAcademico = :idCicloAcademico")
    int eliminarPorCicloAcademico(@Param("idCicloAcademico") Integer idCicloAcademico);


}
