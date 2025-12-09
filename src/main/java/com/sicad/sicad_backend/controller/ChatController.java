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
@RequestMapping("/chat")
@Tag(name = "Chat", description = "Endpoints para la gestión de mensajería entre usuarios")
public class ChatController {

    private final IMensajeService mensajeService;

    // ============ REST Endpoints ============

    @Operation(summary = "Obtener conversación entre dos usuarios")
    @GetMapping("/chat-conversacion/{idUsuario1}/{idUsuario2}")
    public ResponseEntity<BaseListReponse<MensajeResponse>>
    obtenerConversacion(@PathVariable Integer idUsuario1, @PathVariable Integer idUsuario2) {
        BaseListReponse<MensajeResponse> response =
                mensajeService.obtenerConversacion(idUsuario1, idUsuario2);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(summary = "Obtener todas las conversaciones de un usuario")
    @GetMapping("/chat-conversaciones/{idUsuario}")
    public ResponseEntity<BaseListReponse<ConversacionResponse>>
    obtenerConversaciones(@PathVariable Integer idUsuario) {
        BaseListReponse<ConversacionResponse> response =
                mensajeService.obtenerConversaciones(idUsuario);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(summary = "Marcar una conversación como leída")
    @PutMapping("/chat-marcar-leida/{idRemitente}/{idDestinatario}")
    public ResponseEntity<BaseObjectResponse<Void>>
    marcarConversacionComoLeida(@PathVariable Integer idRemitente,
                                @PathVariable Integer idDestinatario) {
        BaseObjectResponse<Void> response =
                mensajeService.marcarConversacionComoLeida(idRemitente, idDestinatario);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(summary = "Enviar mensaje vía REST")
    @PostMapping("/chat-enviar")
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
        }

        return ResponseEntity.status(response.status()).body(response);
    }
}