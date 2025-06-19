package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.MallaCurricular;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IMallaCurricularRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IMallaCurricularService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MallaCurricularServicempl
        extends CRUDImpl<MallaCurricular, Integer>
        implements IMallaCurricularService {

    private final IMallaCurricularRepo repo;

    @Override
    protected IGenericRepo<MallaCurricular, Integer> getRepo() {
        return repo;
    }
}
