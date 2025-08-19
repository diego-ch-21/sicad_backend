package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDisponibilidadRepo extends IGenericRepo<Disponibilidad, Integer> {

    // Busca las disponibilidades de un docente específico en un ciclo académico concreto,
    // siempre que la disponibilidad esté habilitada (enabled = true).
    @Query("SELECT d FROM Disponibilidad d WHERE d.docente.idDocente = :idDocente AND d.cicloAcademico.idCicloAcademico = :idCicloAcademico AND d.enabled = true")
    List<Disponibilidad> buscarPorDocenteYCicloAcademico(@Param("idDocente") Integer idDocente, @Param("idCicloAcademico") Integer idCicloAcademico);

    // Busca todas las disponibilidades de un ciclo académico dado,
    // filtrando solo las que estén habilitadas (enabled = true).
    @Query("SELECT d FROM Disponibilidad d WHERE d.cicloAcademico.idCicloAcademico = :idCicloAcademico AND d.enabled = true")
    List<Disponibilidad> findByCicloAcademico_IdCicloAcademico(@Param("idCicloAcademico") Integer idCicloAcademico);

    // Devuelve todas las disponibilidades que estén habilitadas (enabled = true),
    // sin importar docente ni ciclo académico.
    List<Disponibilidad> findByEnabledTrue();
}

