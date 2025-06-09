package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.persistence.model.Curso;
import com.sicad.sicad_backend.persistence.repository.base.IGenericRepo;
import com.sicad.sicad_backend.persistence.repository.interfaces.ICursoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl
        extends CRUDImpl<Curso, Integer>
        implements ICursoService {

    private final ICursoRepo repo;

    @Override
    protected IGenericRepo<Curso, Integer> getRepo() {
        return repo;
    }
}
