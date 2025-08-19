package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface IPlanDeEstudioRepo extends IGenericRepo<PlanDeEstudio, Integer> {
    boolean existsByCodigo(Integer codigo);
    List<PlanDeEstudio> findByEnabledTrue();
}
