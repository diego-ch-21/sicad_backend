package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IAsignacionRepo extends IGenericRepo<Asignacion, Integer> {
    int deleteByCargaElectiva_IdCargaElectiva(Integer idCargaElectiva);
    List<Asignacion> findByEnabledTrue();

    // Deshabilitar todas las preferencias de un ciclo académico (enabled = false)
    @Modifying
    @Query("UPDATE Preferencia p SET p.enabled = false WHERE p.cicloAcademico.idCicloAcademico = :idCicloAcademico")
    int deshabilitarPorCicloAcademico(@Param("idCicloAcademico") Integer idCicloAcademico);
}
