package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface ICategoriaService extends ICRUD<Categoria, Integer> {
    List<Categoria> findByEnabledTrue();
}
