package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IMallaCurricularRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IMallaCurricularService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MallaCurricularServicempl
        extends CRUDImpl<PlanDeEstudio, Integer>
        implements IMallaCurricularService {

    private final IMallaCurricularRepo repo;

    @Override
    protected IGenericRepo<PlanDeEstudio, Integer> getRepo() {
        return repo;
    }
}
