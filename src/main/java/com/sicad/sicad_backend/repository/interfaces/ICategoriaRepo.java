package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface ICategoriaRepo extends IGenericRepo<Categoria, Integer> {
    List<Categoria> findByEnabledTrue();

}
