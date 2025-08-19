package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IAsignaturaService extends ICRUD<Asignatura, Integer> {
    List<Asignatura> findByEnabledTrue();
}
