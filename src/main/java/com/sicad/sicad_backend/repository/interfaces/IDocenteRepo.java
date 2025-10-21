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

    @Query("SELECT d FROM Docente d WHERE d.usuario = :usuario AND d.enabled = true")
    Optional<Docente> findByUsuarioAndEnabledTrue(@Param("usuario") Usuario usuario);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Docente d WHERE d.codigo = :codigo AND d.enabled = true")
    boolean existsByCodigoAndEnabledTrue(@Param("codigo") String codigo);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " +
            "FROM Docente d WHERE d.idDocente = :idDocente AND d.enabled = true")
    boolean existsByIdDocenteAndEnabledTrue(@Param("idDocente") Integer idDocente);

    @Query("SELECT e FROM Docente e WHERE e.enabled = true")
    List<Docente> findByEnabledTrue();

    @Query("SELECT e FROM Docente e WHERE e.idDocente = :id AND e.enabled = true")
    Optional<Docente> findByIdAndEnabledTrue(@Param("id") Integer idDocente);

    @Query("SELECT e FROM Docente e WHERE e.usuario.idUsuario = :id AND e.enabled = true")
    Optional<Docente> findByIdUsuarioAndEnabledTrue(@Param("id") Integer idUsuario);

}
