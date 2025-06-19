package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.Optional;

public interface IUsuarioRepo extends IGenericRepo<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByCodigo(String codigo);

}
