package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IDedicacionService extends ICRUD<Dedicacion, Integer> {
    List<Dedicacion> findByEnabledTrue();
}
