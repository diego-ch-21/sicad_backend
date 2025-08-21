package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Especializacion;
import com.sicad.sicad_backend.model.PreMatricula;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface IEspecializacionRepo extends IGenericRepo<Especializacion,Integer> {
    List<Especializacion> findByEnabledTrue();
}
