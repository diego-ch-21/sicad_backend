package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.PreMatricula;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IPreMatriculaService extends ICRUD<PreMatricula,Integer> {
    List<PreMatricula> findByEnabledTrue();
    List<PreMatricula> findByEnabledTrueAndCicloAcademico_idCicloAcademico(Integer idCicloAcademico);
}
