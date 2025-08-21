package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Especializacion;
import com.sicad.sicad_backend.model.PreMatricula;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IEspecializacionService extends ICRUD<Especializacion,Integer> {
    List<Especializacion> findByEnabledTrue();
}
