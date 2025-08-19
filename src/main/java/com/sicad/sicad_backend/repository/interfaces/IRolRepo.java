package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface IRolRepo extends IGenericRepo<Rol, Integer> {
    List<Rol> findByEnabledTrue();
}
