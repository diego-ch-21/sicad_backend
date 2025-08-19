package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface ICicloAcademicoService extends ICRUD<CicloAcademico, Integer> {
    List<CicloAcademico> findByEnabledTrue();


}
