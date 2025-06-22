package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IDocenteRepo extends IGenericRepo<Docente, Integer> {
    //optener docente usando el usuario
    Optional<Docente> findByUsuario(Usuario usuario);


}
