package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IAsignaturaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IAsignaturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AsignaturaServiceImpl
    extends CRUDImpl<Asignatura, Integer>
    implements IAsignaturaService {
    private final IAsignaturaRepo repo;
    @Override
    protected IGenericRepo<Asignatura, Integer> getRepo() {
        return repo;
    }
}
