package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IEscuelaService extends ICRUD<Escuela, Integer> {
    List<Escuela> findByEnabledTrue();
}
