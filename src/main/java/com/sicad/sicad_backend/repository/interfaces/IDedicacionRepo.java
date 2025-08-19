package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.repository.base.IGenericRepo;

import java.util.List;

public interface IDedicacionRepo  extends IGenericRepo<Dedicacion, Integer> {
    List<Dedicacion> findByEnabledTrue();

}
