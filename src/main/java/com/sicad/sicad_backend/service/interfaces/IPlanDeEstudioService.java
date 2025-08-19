package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IPlanDeEstudioService extends ICRUD<PlanDeEstudio, Integer> {
    List<PlanDeEstudio> findByEnabledTrue();

}
