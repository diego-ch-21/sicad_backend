package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Especializacion;
import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IPreferenciaRepo extends IGenericRepo<Preferencia, Integer> {

    //remplazarlo luego
    @Query("SELECT p FROM Preferencia p WHERE p.docente.idDocente = :idDocente AND p.cicloAcademico.idCicloAcademico = :idCicloAcademico AND p.enabled = true")
    List<Preferencia> buscarPorDocenteYCicloAcademico(
            @Param("idDocente") Integer idDocente,
            @Param("idCicloAcademico") Integer idCicloAcademico);
    //------------------
    @Query("SELECT e FROM Preferencia e " +
            "WHERE e.enabled = true " +
            "AND e.docente.idDocente = :idDocente " +
            "AND e.cicloAcademico.idCicloAcademico = :idCicloAcademico")
    List<Preferencia> findByEnabledTrueDocenteCicloAcademico(
            @Param("idCicloAcademico") Integer idCicloAcademico,
            @Param("idDocente") Integer idDocente);


    @Query("SELECT e FROM Preferencia e WHERE e.idPreferencia = :id AND e.enabled = true")
    Optional<Preferencia> findByIdAndEnabledTrue(@Param("id") Integer idPreferencia);

    @Query("SELECT COUNT(e) > 0 " +
            "FROM Preferencia e " +
            "WHERE e.docente.idDocente = :idDocente " +
            "AND e.asignatura.idAsignatura = :idAsignatura " +
            "AND e.cicloAcademico.idCicloAcademico = :idCicloAcademico " +
            "AND e.escuela.idEscuela = :idEscuela "+
            "AND e.enabled = true")
    boolean isRestriccionPreferencia(
            @Param("idDocente") Integer idDocente,
            @Param("idAsignatura") Integer idAsignatura,
            @Param("idCicloAcademico") Integer idCicloAcademico,
            @Param("idEscuela") Integer idEscuela);


}
