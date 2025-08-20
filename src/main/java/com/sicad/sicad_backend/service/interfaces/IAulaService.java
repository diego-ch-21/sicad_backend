package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IAulaService extends ICRUD<Aula, Integer> {
    List<Aula> findByEnabledTrue();
}
