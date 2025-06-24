package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICursoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl
        extends CRUDImpl<Asignatura, Integer>
        implements ICursoService {

    private final ICursoRepo repo;

    @Override
    protected IGenericRepo<Asignatura, Integer> getRepo() {
        return repo;
    }
}
