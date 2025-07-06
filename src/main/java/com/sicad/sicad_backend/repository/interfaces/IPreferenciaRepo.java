package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPreferenciaRepo extends IGenericRepo<Preferencia, Integer> {
    @Query("SELECT p FROM Preferencia p WHERE p.docente.idDocente = :idDocente AND p.cargaElectiva.idCargaElectiva = :idCargaElectiva")
    List<Preferencia> buscarPorDocenteYCargaElectiva(@Param("idDocente") Integer idDocente, @Param("idCargaElectiva") Integer idCargaElectiva);

    @Query("SELECT p FROM Preferencia p WHERE p.cargaElectiva.idCargaElectiva = :idCargaElectiva AND p.enabled = true")
    List<Preferencia> findByCargaElectiva_IdCargaElectiva(@Param("idCargaElectiva") Integer idCargaElectiva);
}
