package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Logistica;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ILogisticaRepo extends IGenericRepo<Logistica, Integer> {
    Optional<Logistica> findByUsuario(Usuario usuario);

    @Query("SELECT e FROM Logistica e WHERE e.enabled = true")
    List<Logistica> findByEnabledTrue();

    @Query("SELECT e FROM Logistica e WHERE e.idLogistica = :id AND e.enabled = true")
    Optional<Logistica> findByIdAndEnabledTrue(@Param("id") Integer id);

}
