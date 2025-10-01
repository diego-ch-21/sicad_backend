package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Especializacion;
import com.sicad.sicad_backend.model.PreMatricula;
import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IEspecializacionRepo extends IGenericRepo<Especializacion,Integer> {
    List<Especializacion> findByEnabledTrue();

    @Query("SELECT p FROM Especializacion p WHERE p.docente.idDocente = :idDocente  AND p.enabled = true")
    List<Especializacion> listarEspecializacionesPorDocente(
            @Param("idDocente") Integer idDocente);
}
