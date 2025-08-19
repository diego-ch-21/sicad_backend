package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;


public interface IEscuelaRepo extends IGenericRepo<Escuela, Integer> {
    boolean existsByCodigo(String codigo);
    List<Escuela> findByEnabledTrue();
}
