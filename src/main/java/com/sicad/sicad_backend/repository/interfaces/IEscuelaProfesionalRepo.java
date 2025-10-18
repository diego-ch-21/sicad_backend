package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.EscuelaProfesional;
import com.sicad.sicad_backend.model.EscuelaProfesional;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEscuelaProfesionalRepo extends IGenericRepo<EscuelaProfesional, Integer> {
    @Query("SELECT d FROM EscuelaProfesional d WHERE d.usuario = :usuario")
    Optional<EscuelaProfesional> findByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT e FROM EscuelaProfesional e WHERE e.enabled = true")
    List<EscuelaProfesional> findByEnabledTrue();

    @Query("SELECT e FROM EscuelaProfesional e WHERE e.idEscuelaProfesional = :id AND e.enabled = true")
    Optional<EscuelaProfesional> findByIdAndEnabledTrue(@Param("id") Integer idEscuelaProfesional);
}