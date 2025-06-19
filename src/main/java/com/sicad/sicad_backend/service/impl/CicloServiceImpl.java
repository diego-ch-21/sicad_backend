package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Ciclo;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICicloRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICicloService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CicloServiceImpl
        extends CRUDImpl<Ciclo, Integer>
        implements ICicloService {

    private final ICicloRepo repo;

    @Override
    protected IGenericRepo<Ciclo, Integer> getRepo() {
        return repo;
    }
}
