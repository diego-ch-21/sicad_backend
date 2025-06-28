package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Facultad;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IFacultadRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IFacultadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacultadServiceImpl
    extends CRUDImpl<Facultad, Integer>
    implements IFacultadService {

    private final IFacultadRepo repo;

    @Override
    protected IGenericRepo<Facultad, Integer> getRepo() {
        return repo;
    }

}
