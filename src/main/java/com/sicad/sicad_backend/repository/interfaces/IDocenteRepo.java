package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.model.Docente;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDocenteRepo extends IGenericRepo<Docente, Integer> {
    Optional<Docente> findByUsuario(Usuario usuario);
    boolean existsByCodigo(String codigo);

    boolean existsByIdDocente(Integer idDocente);

    @Query("SELECT e FROM Docente e WHERE e.enabled = true")
    List<Docente> findByEnabledTrue();

    @Query("SELECT e FROM Docente e WHERE e.idDocente = :id AND e.enabled = true")
    Optional<Docente> findByIdAndEnabledTrue(@Param("id") Integer idDocente);

    @Query("SELECT e FROM Docente e WHERE e.usuario.idUsuario = :id AND e.enabled = true")
    Optional<Docente> findByIdUsuarioAndEnabledTrue(@Param("id") Integer idUsuario);

}
