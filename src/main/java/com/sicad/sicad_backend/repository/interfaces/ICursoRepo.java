package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

public interface ICursoRepo extends IGenericRepo<Curso, Integer> {
    boolean existsByCodigo(String codigo);

}
