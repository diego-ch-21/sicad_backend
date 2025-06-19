package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Preferencia;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IPreferenciaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IPreferenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenciaServiceImpl
        extends CRUDImpl<Preferencia, Integer>
        implements IPreferenciaService {

    private final IPreferenciaRepo repo;

    @Override
    protected IGenericRepo<Preferencia, Integer> getRepo() {
        return repo;
    }
}
