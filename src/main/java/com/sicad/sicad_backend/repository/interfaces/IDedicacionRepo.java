package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDedicacionRepo  extends IGenericRepo<Dedicacion, Integer> {
    List<Dedicacion> findByEnabledTrue();


    @Query("SELECT e FROM Dedicacion e WHERE e.idDedicacion = :id AND e.enabled = true")
    Optional<Dedicacion> findByIdAndEnabledTrue(@Param("id") Integer id);

}
