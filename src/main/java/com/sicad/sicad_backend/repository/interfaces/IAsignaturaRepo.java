package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

public interface IAsignaturaRepo extends IGenericRepo<Asignatura, Integer> {
    boolean existsByCodigo(String codigo);
}
