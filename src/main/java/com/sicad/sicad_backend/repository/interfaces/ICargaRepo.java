package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICargaRepo extends IGenericRepo<Carga, Integer> {
    boolean existsByIdCarga(Integer idCarga);


    @Query("SELECT c FROM Carga c " +
            "WHERE c.cicloAcademico.idCicloAcademico = :id " +
            "AND c.enabled = true " +
            "AND c.principal = true")
    Optional<Carga> findPrincipalByCicloAcademicoAndEnabledTrue(
            @Param("id") Integer idCicloAcademico
    );


    @Query("SELECT e FROM Carga e WHERE e.enabled = true")
    List<Carga> findByEnabledTrue();

    @Query("SELECT e FROM Carga e WHERE e.idCarga = :id AND e.enabled = true")
    Optional<Carga> findByIdAndEnabledTrue(@Param("id") Integer idCarga);

    @Query("SELECT e FROM Carga e WHERE e.cicloAcademico.idCicloAcademico = :id AND e.enabled = true")
    List<Carga> findByIdCicloAcademicoAndEnabledTrue(@Param("id") Integer idCicloAcademico);




}
