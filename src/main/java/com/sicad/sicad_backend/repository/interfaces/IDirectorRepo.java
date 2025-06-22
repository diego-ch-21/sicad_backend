package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.Optional;

public interface IDirectorRepo extends IGenericRepo<Director, Integer> {
    Optional<Director> findByUsuario(Usuario usuario);
}
