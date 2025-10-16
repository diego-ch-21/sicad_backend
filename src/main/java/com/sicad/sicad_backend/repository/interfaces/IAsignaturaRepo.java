package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAsignaturaRepo extends IGenericRepo<Asignatura, Integer> {
    boolean existsByCodigo(String codigo);

    @Query("SELECT e FROM Asignatura e WHERE e.enabled = true")
    List<Asignatura> findByEnabledTrue();

    @Query("SELECT e FROM Asignatura e WHERE e.idAsignatura = :id AND e.enabled = true")
    Optional<Asignatura> findByIdAndEnabledTrue(@Param("id") Integer id);


}
