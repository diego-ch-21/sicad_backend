package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.ICategoriaRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.ICategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl
        extends CRUDImpl<Categoria, Integer>
        implements ICategoriaService {

    private final ICategoriaRepo repo;

    @Override
    protected IGenericRepo<Categoria, Integer> getRepo() {
        return repo;
    }
}
