package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Algoritmo;
import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IAlgoritmoService extends ICRUD<Algoritmo, Integer> {
    List<Algoritmo> findByEnabledTrue();
}
