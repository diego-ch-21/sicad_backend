package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IAsignacionService extends ICRUD<Asignacion, Integer> {
    List<Asignacion> findByEnabledTrue();

}
