package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface ICargaRepo extends IGenericRepo<Carga, Integer> {
    List<Carga> findByEnabledTrue();
    List<Carga> findByEnabledTrueAndCicloAcademico_IdCicloAcademico(Integer idCicloAcademico);
}
