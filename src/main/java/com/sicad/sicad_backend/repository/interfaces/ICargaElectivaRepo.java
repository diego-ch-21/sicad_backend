package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.CargaElectiva;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.Optional;

public interface ICargaElectivaRepo extends IGenericRepo<CargaElectiva, Integer> {
    Optional<CargaElectiva> findByCicloAcademico_IdCicloAcademico(Integer idCicloAcademico);
}
