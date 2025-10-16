package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IRolRepo extends IGenericRepo<Rol, Integer> {
    @Query("SELECT e FROM Rol e WHERE e.enabled = true")
    List<Rol> findByEnabledTrue();

    @Query("SELECT e FROM Rol e WHERE e.idRol = :id AND e.enabled = true")
    Optional<Rol> findByIdAndEnabledTrue(@Param("id") Integer idRol);
}
