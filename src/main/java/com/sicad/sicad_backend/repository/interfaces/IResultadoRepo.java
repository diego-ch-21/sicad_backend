package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Resultado;
import com.sicad.sicad_backend.model.Rol;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IResultadoRepo extends IGenericRepo<Resultado, Integer> {
    @Query("SELECT e FROM Resultado e WHERE e.idResultado = :id AND e.enabled = true")
    Optional<Resultado> findByIdAndEnabledTrue(@Param("id") Integer idResultado);
}
