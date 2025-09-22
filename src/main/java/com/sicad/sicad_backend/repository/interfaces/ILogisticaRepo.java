package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.JefeDepartamento;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.Optional;

public interface ILogisticaRepo extends IGenericRepo<Logistica, Integer> {
    Optional<Logistica> findByUsuario(Usuario usuario);

}
