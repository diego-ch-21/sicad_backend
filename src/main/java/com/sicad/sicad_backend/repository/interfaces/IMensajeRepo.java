package com.sicad.sicad_backend.repository.interfaces;

import com.sicad.sicad_backend.model.Mensaje;
import com.sicad.sicad_backend.repository.base.IGenericRepo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMensajeRepo extends IGenericRepo<Mensaje, Integer> {

    // Obtener conversación entre dos usuarios
    @Query("SELECT m FROM Mensaje m WHERE " +
            "((m.remitente.idUsuario = :idUsuario1 AND m.destinatario.idUsuario = :idUsuario2) OR " +
            "(m.remitente.idUsuario = :idUsuario2 AND m.destinatario.idUsuario = :idUsuario1)) " +
            "AND m.enabled = true ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversacion(@Param("idUsuario1") Integer idUsuario1,
                                   @Param("idUsuario2") Integer idUsuario2);

    // Obtener mensajes no leídos de un usuario
    @Query("SELECT m FROM Mensaje m WHERE m.destinatario.idUsuario = :idUsuario " +
            "AND m.leido = false AND m.enabled = true ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findMensajesNoLeidos(@Param("idUsuario") Integer idUsuario);

    // Contar mensajes no leídos entre dos usuarios
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.remitente.idUsuario = :idRemitente " +
            "AND m.destinatario.idUsuario = :idDestinatario AND m.leido = false AND m.enabled = true")
    Integer contarMensajesNoLeidos(@Param("idRemitente") Integer idRemitente,
                                   @Param("idDestinatario") Integer idDestinatario);

    // Obtener último mensaje de una conversación
    @Query("SELECT m FROM Mensaje m WHERE " +
            "((m.remitente.idUsuario = :idUsuario1 AND m.destinatario.idUsuario = :idUsuario2) OR " +
            "(m.remitente.idUsuario = :idUsuario2 AND m.destinatario.idUsuario = :idUsuario1)) " +
            "AND m.enabled = true ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findUltimoMensaje(@Param("idUsuario1") Integer idUsuario1,
                                    @Param("idUsuario2") Integer idUsuario2);

    // Obtener todas las conversaciones de un usuario
    @Query("SELECT DISTINCT CASE " +
            "WHEN m.remitente.idUsuario = :idUsuario THEN m.destinatario.idUsuario " +
            "ELSE m.remitente.idUsuario END " +
            "FROM Mensaje m WHERE " +
            "(m.remitente.idUsuario = :idUsuario OR m.destinatario.idUsuario = :idUsuario) " +
            "AND m.enabled = true")
    List<Integer> findUsuariosConConversacion(@Param("idUsuario") Integer idUsuario);
}