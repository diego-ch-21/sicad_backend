package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.persistence.model.HorarioCurso;
import com.sicad.sicad_backend.persistence.repository.base.IGenericRepo;
import com.sicad.sicad_backend.persistence.repository.interfaces.IHorarioCursoRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IHorarioCursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HorarioCursoServiceImpl
        extends CRUDImpl<HorarioCurso, Integer>
        implements IHorarioCursoService {

    private final IHorarioCursoRepo repo;

    @Override
    protected IGenericRepo<HorarioCurso, Integer> getRepo() {
        return repo;
    }
}
