package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IDocenteService extends ICRUD<Docente, Integer> {
    List<Docente> findByEnabledTrue();
}
