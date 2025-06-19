package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IDirectorRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IDirectorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl
        extends CRUDImpl<Director, Integer>
        implements IDirectorService {

    private final IDirectorRepo repo;

    @Override
    protected IGenericRepo<Director, Integer> getRepo() {
        return repo;
    }
}
