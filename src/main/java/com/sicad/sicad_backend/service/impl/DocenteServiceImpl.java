package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDocenteRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDocenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocenteServiceImpl
        extends CRUDImpl<Docente, Integer>
        implements IDocenteService {

    private final IDocenteRepo repo;

    @Override
    protected IGenericRepo<Docente, Integer> getRepo() {
        return repo;
    }
}
