package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ICicloAcademicoRepo extends IGenericRepo<CicloAcademico, Integer> {
    boolean existsByNombre(String nombre);
    boolean existsByIdCicloAcademico(Integer idCicloAcademico);


    @Query("SELECT e FROM CicloAcademico e WHERE e.enabled = true")
    List<CicloAcademico> findByEnabledTrue();

    @Query("SELECT e FROM CicloAcademico e WHERE e.idCicloAcademico = :id AND e.enabled = true")
    Optional<CicloAcademico> findByIdAndEnabledTrue(@Param("id") Integer idCicloAcademico);

}
