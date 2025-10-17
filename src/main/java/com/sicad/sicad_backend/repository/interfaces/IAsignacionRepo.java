package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAsignacionRepo extends IGenericRepo<Asignacion, Integer> {

    @Query("SELECT e FROM Asignacion e WHERE e.enabled = true")
    List<Asignacion> findByEnabledTrue();

    @Query("SELECT e FROM Asignacion e WHERE e.idAsignacion = :id AND e.enabled = true")
    Optional<Asignacion> findByIdAndEnabledTrue(@Param("id") Integer idAsignacion);



    @Query("""
    SELECT a FROM Asignacion a
    WHERE a.docente.idDocente = :idDocente
      AND a.carga.idCarga = :idCarga
      AND a.enabled = true
    """)
    List<Asignacion> findByDocenteAndCargaEnabled(
            @Param("idDocente") Integer idDocente,
            @Param("idCarga") Integer idCarga
    );

    @Query("""
    SELECT a FROM Asignacion a
    WHERE a.carga.idCarga = :idCarga
      AND a.curso.escuela.idEscuela = :idEscuela
      AND a.enabled = true
    """)
    List<Asignacion> findByCargaAndEscuelaEnabled(
            @Param("idCarga") Integer idCarga,
            @Param("idEscuela") Integer idEscuela
    );

    @Query("""
    SELECT a FROM Asignacion a
    WHERE a.carga.idCarga = :idCarga
      AND a.enabled = true
    """)
    List<Asignacion> findByEscuelaEnabled(
            @Param("idCarga") Integer idCarga
    );



}
