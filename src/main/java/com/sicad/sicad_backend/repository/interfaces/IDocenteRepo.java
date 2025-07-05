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

    @Query("""
    SELECT DISTINCT d FROM Docente d
    LEFT JOIN FETCH d.preferencias p
    WHERE p.cargaElectiva.idCargaElectiva = :idCargaElectiva
""")
    List<Docente> findAllWithPreferenciasByCargaElectiva(@Param("idCargaElectiva") Integer idCargaElectiva);

    @Query("SELECT DISTINCT c FROM Docente c LEFT JOIN FETCH c.preferencias")
    List<Docente> findAllWithDocentes();
}
