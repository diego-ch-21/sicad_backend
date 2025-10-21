package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.PlanDeEstudio;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IPlanDeEstudioRepo extends IGenericRepo<PlanDeEstudio, Integer> {

    @Query("SELECT e FROM PlanDeEstudio e WHERE e.enabled = true")
    List<PlanDeEstudio> findByEnabledTrue();

    @Query("SELECT e FROM PlanDeEstudio e WHERE e.idPlanDeEstudio = :id AND e.enabled = true")
    Optional<PlanDeEstudio> findByIdAndEnabledTrue(@Param("id") Integer id);
}
