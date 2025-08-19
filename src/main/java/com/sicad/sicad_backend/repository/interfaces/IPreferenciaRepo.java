package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPreferenciaRepo extends IGenericRepo<Preferencia, Integer> {

    /**
     * Busca todas las preferencias de un docente específico
     * dentro de un ciclo académico determinado.
     *
     * @param idDocente ID del docente
     * @param idCicloAcademico ID del ciclo académico
     * @return Lista de preferencias activas (enabled = true)
     *         del docente en el ciclo académico indicado.
     */
    @Query("SELECT p FROM Preferencia p WHERE p.docente.idDocente = :idDocente AND p.cicloAcademico.idCicloAcademico = :idCicloAcademico AND p.enabled = true")
    List<Preferencia> buscarPorDocenteYCicloAcademico(
            @Param("idDocente") Integer idDocente,
            @Param("idCicloAcademico") Integer idCicloAcademico);


    /**
     * Busca todas las preferencias de un ciclo académico específico.
     *
     * @param idCicloAcademico ID del ciclo académico
     * @return Lista de preferencias activas (enabled = true)
     *         asociadas al ciclo académico.
     */
    @Query("SELECT p FROM Preferencia p WHERE p.cicloAcademico.idCicloAcademico = :idCicloAcademico AND p.enabled = true")
    List<Preferencia> findByCicloAcademico_IdCicloAcademico(@Param("idCicloAcademico") Integer idCicloAcademico);


    /**
     * Obtiene todas las preferencias que están habilitadas.
     *
     * @return Lista de preferencias con enabled = true
     */
    List<Preferencia> findByEnabledTrue();
}
