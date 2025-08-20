package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.base.CRUDImpl;
import com.sicad.sicad_backend.service.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl
        extends CRUDImpl<Usuario, Integer>
        implements IUsuarioService {

    private final IUsuarioRepo usuarioRepo;

    @Override
    protected IGenericRepo<Usuario, Integer> getRepo() {
        return usuarioRepo;
    }


    @Override
    public List<Usuario> findByEnabledTrue() {
        return usuarioRepo.findByEnabledTrue();
    }
}
