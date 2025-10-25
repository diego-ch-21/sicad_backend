package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.CicloAcademico;
import com.sicad.sicad_backend.model.Notificacion;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface INotificacionRepo extends IGenericRepo<Notificacion, Integer> {
    @Query("SELECT e FROM Notificacion e WHERE e.enabled = true")
    List<Notificacion> findByEnabledTrue();

    @Query("SELECT e FROM Notificacion e WHERE e.idNotificacion = :id AND e.enabled = true")
    Optional<Notificacion> findByIdAndEnabledTrue(@Param("id") Integer idNotificacion);

    @Query("SELECT n FROM Notificacion n " +
            "WHERE n.enabled = true AND n.usuarioDestino.idUsuario = :idUsuario " +
            "ORDER BY n.createdAt DESC")
    List<Notificacion> findByUsuarioDestinoIdAndEnabledTrueOrderByCreatedAtDesc(@Param("idUsuario") Integer idUsuario);

}
