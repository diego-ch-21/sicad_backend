package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.persistence.model.Disponibilidad;
import com.sicad.sicad_backend.persistence.repository.base.IGenericRepo;
import com.sicad.sicad_backend.persistence.repository.interfaces.IDisponibilidadRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDisponibilidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DisponibilidadServiceImpl
        extends CRUDImpl<Disponibilidad, Integer>
        implements IDisponibilidadService {

    private final IDisponibilidadRepo repo;

    @Override
    protected IGenericRepo<Disponibilidad, Integer> getRepo() {
        return repo;
    }
}
