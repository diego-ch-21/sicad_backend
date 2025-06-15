package com.sicad.sicad_backend.persistence.repository.interfaces;

import com.sicad.sicad_backend.persistence.model.Usuario;
import com.sicad.sicad_backend.persistence.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IUsuarioRepo extends IGenericRepo<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    boolean existsByCodigo(String codigo);

}
