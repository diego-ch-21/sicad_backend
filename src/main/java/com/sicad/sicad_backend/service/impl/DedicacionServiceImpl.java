package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDedicacionRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDedicacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DedicacionServiceImpl
        extends CRUDImpl<Dedicacion, Integer>
        implements IDedicacionService {

    private final IDedicacionRepo repo;

    @Override
    protected IGenericRepo<Dedicacion, Integer> getRepo() {
        return repo;
    }
}
