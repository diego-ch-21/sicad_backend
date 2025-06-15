package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IRolRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IRolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolServiceImpl
        extends CRUDImpl<Rol, Integer>
        implements IRolService {

    private final IRolRepo repo;

    @Override
    protected IGenericRepo<Rol, Integer> getRepo() {
        return repo;
    }
}
