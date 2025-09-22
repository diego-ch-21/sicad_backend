package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.model.JefeDepartamento;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.Optional;

public interface IJefeDepartamentoRepo extends IGenericRepo<JefeDepartamento, Integer> {
    Optional<JefeDepartamento> findByUsuario(Usuario usuario);

}
