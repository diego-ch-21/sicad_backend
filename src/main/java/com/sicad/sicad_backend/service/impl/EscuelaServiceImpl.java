package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IEscuelaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IEscuelaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EscuelaServiceImpl
        extends CRUDImpl<Escuela, Integer>
        implements IEscuelaService {

    private final IEscuelaRepo repo;

    @Override
    protected IGenericRepo<Escuela, Integer> getRepo() {
        return repo;
    }
}
