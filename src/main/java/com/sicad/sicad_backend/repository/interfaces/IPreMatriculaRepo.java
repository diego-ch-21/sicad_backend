package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.PreMatricula;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPreMatriculaRepo extends IGenericRepo<PreMatricula,Integer> {
    List<PreMatricula> findByEnabledTrue();
    @Query("SELECT p FROM PreMatricula p " +
            "WHERE p.enabled = true " +
            "AND p.cicloAcademico.idCicloAcademico = :idCicloAcademico")
    List<PreMatricula> findPreMatriculasActivasPorCiclo(@Param("idCicloAcademico") Integer idCicloAcademico);

}
