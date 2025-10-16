package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Escuela;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepo extends IGenericRepo<Usuario, Integer> {
    @Query("SELECT u FROM Usuario u WHERE u.email = :email AND u.enabled = true")
    Optional<Usuario> findByEmailAndEnabledTrue(@Param("email") String email);

    boolean existsByCodigo(String codigo);


    @Query("SELECT e FROM Usuario e WHERE e.enabled = true")
    List<Usuario> findByEnabledTrue();

    @Query("SELECT e FROM Usuario e WHERE e.idUsuario = :id AND e.enabled = true")
    Optional<Usuario> findByIdAndEnabledTrue(@Param("id") Integer idUsuario);

}
