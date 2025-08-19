package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ICursoRepo extends IGenericRepo<Curso, Integer> {

    // Verifica si ya existe un curso con el código proporcionado (para evitar duplicados).
    boolean existsByCodigo(String codigo);

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
     * Recupera todos los cursos junto con su relación cursoHorario usando LEFT JOIN FETCH.
     * El DISTINCT se usa para evitar duplicados cuando un curso tiene varios horarios.
     */
    @Query("SELECT DISTINCT c FROM Curso c LEFT JOIN FETCH c.cursoHorario")
    List<Curso> findAllWithHorarios();

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

    /**
     * Obtiene todos los cursos habilitados (enabled = true).
     */
    List<Curso> findByEnabledTrue();
}
