package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.Enum.TipoMensaje;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.mensaje.ConversacionResponse;
import com.sicad.sicad_backend.dto.mensaje.MensajeRequest;
import com.sicad.sicad_backend.dto.mensaje.MensajeResponse;
import com.sicad.sicad_backend.dto.mensaje.NotificacionMensaje;
import com.sicad.sicad_backend.service.interfaces.IMensajeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Endpoints para la gestión de mensajería entre usuarios")
public class ChatController {

    private final IMensajeService mensajeService;
    private final SimpMessagingTemplate messagingTemplate;

    // ============ WebSocket Endpoints ============

    /**
     * Envía un mensaje a través de WebSocket
     * El cliente envía a: /app/chat.enviar
     * El mensaje se distribuye a: /user/{idDestinatario}/queue/mensajes
     */
    @MessageMapping("/chat.enviar")
    public void enviarMensajeWebSocket(@Payload @Valid MensajeRequest request) {
        BaseObjectResponse<MensajeResponse> response = mensajeService.enviarMensaje(request);

        if (response.status() == 201) {
            MensajeResponse mensaje = response.data();

            // Notificar al destinatario
            NotificacionMensaje notificacion = NotificacionMensaje.builder()
                    .tipo(TipoMensaje.MENSAJE)
                    .mensaje(mensaje)
                    .build();

            messagingTemplate.convertAndSendToUser(
                    mensaje.getIdDestinatario().toString(),
                    "/queue/mensajes",
                    notificacion
            );

            // Confirmar al remitente
            messagingTemplate.convertAndSendToUser(
                    mensaje.getIdRemitente().toString(),
                    "/queue/mensajes",
                    notificacion
            );
        }
    }

    /**
     * Notifica que un usuario está escribiendo
     * El cliente envía a: /app/chat.escribiendo
     */
    @MessageMapping("/chat.escribiendo")
    public void notificarEscribiendo(@Payload NotificacionMensaje notificacion) {
        messagingTemplate.convertAndSendToUser(
                notificacion.getIdUsuario().toString(),
                "/queue/mensajes",
                NotificacionMensaje.builder()
                        .tipo(TipoMensaje.ESCRIBIENDO)
                        .idUsuario(notificacion.getIdUsuario())
                        .build()
        );
    }

    /**
     * Marca mensajes como leídos
     * El cliente envía a: /app/chat.leido
     */
    @MessageMapping("/chat.leido")
    public void marcarComoLeido(@Payload NotificacionMensaje notificacion) {
        if (notificacion.getMensaje() != null && notificacion.getMensaje().getIdMensaje() != null) {
            mensajeService.marcarComoLeido(notificacion.getMensaje().getIdMensaje());

            // Notificar al remitente que su mensaje fue leído
            messagingTemplate.convertAndSendToUser(
                    notificacion.getMensaje().getIdRemitente().toString(),
                    "/queue/mensajes",
                    NotificacionMensaje.builder()
                            .tipo(TipoMensaje.LEIDO)
                            .mensaje(notificacion.getMensaje())
                            .build()
            );
        }
    }

    // ============ REST Endpoints ============

    @Operation(summary = "Obtener conversación entre dos usuarios")
    @GetMapping("/chat/conversacion/{idUsuario1}/{idUsuario2}")
    public ResponseEntity<BaseListReponse<MensajeResponse>>
    obtenerConversacion(@PathVariable Integer idUsuario1, @PathVariable Integer idUsuario2) {
        BaseListReponse<MensajeResponse> response =
                mensajeService.obtenerConversacion(idUsuario1, idUsuario2);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(summary = "Obtener todas las conversaciones de un usuario")
    @GetMapping("/chat/conversaciones/{idUsuario}")
    public ResponseEntity<BaseListReponse<ConversacionResponse>>
    obtenerConversaciones(@PathVariable Integer idUsuario) {
        BaseListReponse<ConversacionResponse> response =
                mensajeService.obtenerConversaciones(idUsuario);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(summary = "Marcar una conversación como leída")
    @PutMapping("/chat/marcar-leida/{idRemitente}/{idDestinatario}")
    public ResponseEntity<BaseObjectResponse<Void>>
    marcarConversacionComoLeida(@PathVariable Integer idRemitente,
                                @PathVariable Integer idDestinatario) {
        BaseObjectResponse<Void> response =
                mensajeService.marcarConversacionComoLeida(idRemitente, idDestinatario);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(summary = "Enviar mensaje vía REST")
    @PostMapping("/chat/enviar")
    public ResponseEntity<BaseObjectResponse<MensajeResponse>>
    enviarMensajeRest(@Valid @RequestBody MensajeRequest request) {
        BaseObjectResponse<MensajeResponse> response = mensajeService.enviarMensaje(request);

        // También enviar notificación WebSocket si está conectado
        if (response.status() == 201) {
            MensajeResponse mensaje = response.data();
            NotificacionMensaje notificacion = NotificacionMensaje.builder()
                    .tipo(TipoMensaje.MENSAJE)
                    .mensaje(mensaje)
                    .build();

            messagingTemplate.convertAndSendToUser(
                    mensaje.getIdDestinatario().toString(),
                    "/queue/mensajes",
                    notificacion
            );
        }

        return ResponseEntity.status(response.status()).body(response);
    }
}