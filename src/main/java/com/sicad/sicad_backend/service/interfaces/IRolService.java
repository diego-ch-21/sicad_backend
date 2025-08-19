package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.service.base.ICRUD;

import java.util.List;

public interface IRolService extends ICRUD<Rol, Integer> {
    List<Rol> findByEnabledTrue();

}
