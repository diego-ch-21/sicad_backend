package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.persistence.model.Usuario;
import com.sicad.sicad_backend.persistence.repository.base.IGenericRepo;
import com.sicad.sicad_backend.persistence.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.presentation.dto.UsuarioDTO;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IUsuarioService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl
        extends CRUDImpl<Usuario, Integer>
        implements IUsuarioService {

    private final IUsuarioRepo repo;

    @Override
    protected IGenericRepo<Usuario, Integer> getRepo() {
        return repo;
    }


}
