package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Director;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDirectorRepo extends IGenericRepo<Director, Integer> {
    @Query("SELECT d FROM Director d WHERE d.usuario = :usuario")
    Optional<Director> findByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT COUNT(e) > 0 FROM Director e WHERE e.codigo = :codigo")
    boolean existsByCodigo(@Param("codigo") String codigo);

    @Query("SELECT e FROM Director e WHERE e.enabled = true")
    List<Director> findByEnabledTrue();

    @Query("SELECT e FROM Director e WHERE e.idDirector = :id AND e.enabled = true")
    Optional<Director> findByIdAndEnabledTrue(@Param("id") Integer id);
}
