package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface IEscuelaRepo extends IGenericRepo<Escuela, Integer> {
    @Query("SELECT COUNT(e) > 0 FROM Escuela e WHERE e.codigo = :codigo")
    boolean existsByCodigo(@Param("codigo") String codigo);

    @Query("SELECT e FROM Escuela e WHERE e.enabled = true")
    List<Escuela> findByEnabledTrue();

    @Query("SELECT e FROM Escuela e WHERE e.idEscuela = :id AND e.enabled = true")
    Optional<Escuela> findByIdAndEnabledTrue(@Param("id") Integer id);
}
