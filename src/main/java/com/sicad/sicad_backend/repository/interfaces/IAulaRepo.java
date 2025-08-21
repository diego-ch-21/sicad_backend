package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface IAulaRepo extends IGenericRepo<Aula, Integer> {
    List<Aula> findByEnabledTrue();
    boolean existsByCodigo(String codigo);


}
