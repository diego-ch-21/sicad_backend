package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface IAsignaturaRepo extends IGenericRepo<Asignatura, Integer> {
    boolean existsByCodigo(String codigo);
    List<Asignatura> findByEnabledTrue();
}
