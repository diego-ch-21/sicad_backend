package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Categoria;
import com.sicad.sicad_backend.model.Dedicacion;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICategoriaRepo extends IGenericRepo<Categoria, Integer> {
    @Query("SELECT e FROM Categoria e WHERE e.enabled = true")
    List<Categoria> findByEnabledTrue();

    @Query("SELECT e FROM Categoria e WHERE e.idCategoria = :id AND e.enabled = true")
    Optional<Categoria> findByIdAndEnabledTrue(@Param("id") Integer idCategoria);


}
