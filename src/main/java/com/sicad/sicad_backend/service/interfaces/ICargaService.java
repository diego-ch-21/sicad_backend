package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;
import java.util.Optional;

public interface ICargaService extends ICRUD<Carga, Integer> {
    List<Carga> findByEnabledTrue();
    //cargas de un idCicloAcademicoEspecifico, que sean true
    List<Carga> findByEnabledTrueAndCicloAcademico_Id(Integer idCicloAcademico);


}
