package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignacion;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IAsignacionRepo extends IGenericRepo<Asignacion, Integer> {
    List<Asignacion> findByEnabledTrue();

}
