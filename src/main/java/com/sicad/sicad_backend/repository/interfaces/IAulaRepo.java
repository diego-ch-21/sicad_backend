package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Asignatura;
import com.sicad.sicad_backend.model.Aula;
import com.sicad.sicad_backend.model.Carga;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IAulaRepo extends IGenericRepo<Aula, Integer> {
    List<Aula> findByEnabledTrue();
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Aula a WHERE a.nombre = :nombre AND a.enabled = true")
    boolean existsNombre(@Param("nombre") String nombre);


}
