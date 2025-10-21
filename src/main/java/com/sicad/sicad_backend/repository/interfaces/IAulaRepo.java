package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IAulaRepo extends IGenericRepo<Aula, Integer> {

    @Query("SELECT a FROM Aula a WHERE a.nombre = :nombre AND a.enabled = true")
    Optional<Aula> findByNombreAndEnabledTrue(@Param("nombre") String nombre);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Aula a WHERE a.nombre = :nombre AND a.enabled = true")
    boolean existsNombre(@Param("nombre") String nombre);

    @Query("SELECT e FROM Aula e WHERE e.enabled = true")
    List<Aula> findByEnabledTrue();

    @Query("SELECT e FROM Aula e WHERE e.idAula= :id AND e.enabled = true")
    Optional<Aula> findByIdAndEnabledTrue(@Param("id") Integer idAula);
}
