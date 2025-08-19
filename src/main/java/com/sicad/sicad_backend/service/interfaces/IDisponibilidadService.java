package com.sicad.sicad_backend.service.interfaces;


import com.sicad.sicad_backend.model.Disponibilidad;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IDisponibilidadService extends ICRUD<Disponibilidad, Integer> {
    List<Disponibilidad> findByEnabledTrue();

}
