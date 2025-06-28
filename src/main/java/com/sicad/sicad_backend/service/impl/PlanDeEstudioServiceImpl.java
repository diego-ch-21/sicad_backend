package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IPlanDeEstudioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IPlanDeEstudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlanDeEstudioServiceImpl
    extends CRUDImpl<PlanDeEstudio, Integer>
    implements IPlanDeEstudioService {
    private final IPlanDeEstudioRepo repo;
    @Override
    protected IGenericRepo<PlanDeEstudio, Integer> getRepo() {
        return repo;
    }
}
