package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.model.JefeDepartamento;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IJefeDepartamentoService extends ICRUD<JefeDepartamento, Integer> {
    List<JefeDepartamento> findByEnabledTrue();

}
