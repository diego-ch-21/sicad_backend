package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.DepartamentoAcademico;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDepartamentoAcademicoRepo extends IGenericRepo<DepartamentoAcademico, Integer> {

    @Query("SELECT d FROM DepartamentoAcademico d WHERE d.usuario = :usuario")
    Optional<DepartamentoAcademico> findByUsuario(@Param("usuario") Usuario usuario);

    @Query("SELECT e FROM DepartamentoAcademico e WHERE e.enabled = true")
    List<DepartamentoAcademico> findByEnabledTrue();

    @Query("SELECT e FROM DepartamentoAcademico e WHERE e.idDepartamentoAcademico = :id AND e.enabled = true")
    Optional<DepartamentoAcademico> findByIdAndEnabledTrue(@Param("id") Integer idDepartamentoAcademico);
}
