package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Algoritmo;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;
import java.util.Optional;

public interface IAlgoritmoRepo extends IGenericRepo<Algoritmo, Integer> {
    List<Algoritmo> findByEnabledTrue();
    Optional<Algoritmo> findByPrincipalTrue();
}
