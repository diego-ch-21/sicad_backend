package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Curso;
import com.sicad.sicad_backend.model.Docente;
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

    @Query("SELECT DISTINCT c FROM Docente c LEFT JOIN FETCH c.preferencias")
    List<Docente> findAllWithDocentesPreferencia();

    @Query("SELECT DISTINCT c FROM Docente c LEFT JOIN FETCH c.disponibilidad")
    List<Docente> findAllWithDocentesDisponibilidad();

    @Query("SELECT DISTINCT c FROM Docente c LEFT JOIN FETCH c.asignaciones")
    List<Docente> findAllWithDocentesAsignacion();

    @Query("SELECT DISTINCT c FROM Docente c LEFT JOIN FETCH c.asignaciones WHERE c.idDocente = :idDocente")
    Optional<Docente> findDocenteWithAsignacionesById(@Param("idDocente") Integer idDocente);

    @Query("SELECT d FROM Docente d WHERE d.enabled = true")
    List<Docente> findAllEnabledDocentes();
}
