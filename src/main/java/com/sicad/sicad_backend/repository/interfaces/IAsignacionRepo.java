package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

public interface IAsignacionRepo extends IGenericRepo<Asignacion, Integer> {
    int deleteByCargaElectiva_IdCargaElectiva(Integer idCargaElectiva);
}
