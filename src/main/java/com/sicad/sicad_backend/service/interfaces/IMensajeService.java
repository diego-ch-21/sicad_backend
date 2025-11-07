package com.sicad.sicad_backend.service.interfaces;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.mensaje.ConversacionResponse;
import com.sicad.sicad_backend.dto.mensaje.MensajeRequest;
import com.sicad.sicad_backend.dto.mensaje.MensajeResponse;

public interface IMensajeService {
    BaseObjectResponse<MensajeResponse> enviarMensaje(MensajeRequest request);
    BaseListReponse<MensajeResponse> obtenerConversacion(Integer idUsuario1, Integer idUsuario2);
    BaseListReponse<ConversacionResponse> obtenerConversaciones(Integer idUsuario);
    BaseObjectResponse<Void> marcarComoLeido(Integer idMensaje);
    BaseObjectResponse<Void> marcarConversacionComoLeida(Integer idRemitente, Integer idDestinatario);
}