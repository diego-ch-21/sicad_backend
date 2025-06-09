package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.persistence.model.Asignacion;
import com.sicad.sicad_backend.persistence.repository.base.IGenericRepo;
import com.sicad.sicad_backend.persistence.repository.interfaces.IAsignacionRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsignacionServiceImpl
        extends CRUDImpl<Asignacion, Integer>
        implements IAsignacionService {

    private final IAsignacionRepo repo;

    @Override
    protected IGenericRepo<Asignacion, Integer> getRepo() {
        return repo;
    }
}
