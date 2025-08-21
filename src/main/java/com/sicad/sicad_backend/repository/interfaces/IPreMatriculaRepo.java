package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.PreMatricula;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface IPreMatriculaRepo extends IGenericRepo<PreMatricula,Integer> {
    List<PreMatricula> findByEnabledTrue();
    List<PreMatricula> findByEnabledTrueAndCicloAcademico_idCicloAcademico(Integer idCicloAcademico);
}
