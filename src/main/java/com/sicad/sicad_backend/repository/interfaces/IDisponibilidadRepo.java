package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IDisponibilidadRepo extends IGenericRepo<Disponibilidad, Integer> {
    @Query("SELECT d FROM Disponibilidad d WHERE d.docente.idDocente = :idDocente AND d.cargaElectiva.idCargaElectiva = :idCargaElectiva")
    List<Disponibilidad> buscarPorDocenteYCargaElectiva(@Param("idDocente") Integer idDocente, @Param("idCargaElectiva") Integer idCargaElectiva);

    @Query("SELECT d FROM Disponibilidad d WHERE d.cargaElectiva.idCargaElectiva = :idCargaElectiva AND d.enabled = true")
    List<Disponibilidad> findByCargaElectiva_IdCargaElectiva(@Param("idCargaElectiva") Integer idCargaElectiva);
}
