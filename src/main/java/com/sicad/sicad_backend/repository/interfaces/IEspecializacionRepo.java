package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Especializacion;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IEspecializacionRepo extends IGenericRepo<Especializacion,Integer> {
    @Query("SELECT e FROM Especializacion e WHERE e.enabled = true")
    List<Especializacion> findByEnabledTrue();

    @Query("SELECT e FROM Especializacion e WHERE e.idEspecializacion = :id AND e.enabled = true")
    Optional<Especializacion> findByIdAndEnabledTrue(@Param("id") Integer idEspecializacion);

    @Query("SELECT p FROM Especializacion p WHERE p.docente.idDocente = :id  AND p.enabled = true")
    List<Especializacion> listarEspecializacionesPorDocente(@Param("id") Integer idDocente);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
            "FROM Especializacion e " +
            "WHERE e.docente.idDocente = :idDocente " +
            "AND e.asignatura.idAsignatura = :idAsignatura " +
            "AND e.enabled = true")
    boolean isReglaAsignacionAsignatura(@Param("idDocente") Integer idDocente,
                                        @Param("idAsignatura") Integer idAsignatura);
}
