package com.sicad.sicad_backend.persistence.repository.interfaces;

import com.sicad.sicad_backend.persistence.model.Docente;
import com.sicad.sicad_backend.persistence.model.Usuario;
import com.sicad.sicad_backend.persistence.repository.base.IGenericRepo;

import java.util.Optional;

public interface IDocenteRepo extends IGenericRepo<Docente, Integer> {
    //optener docente usando el usuario
    Optional<Docente> findByIdUsuario(Usuario usuario);

}
