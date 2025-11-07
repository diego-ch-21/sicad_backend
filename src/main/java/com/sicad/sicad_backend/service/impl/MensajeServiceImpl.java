package com.sicad.sicad_backend.service.impl;

import com.sicad.sicad_backend.Enum.Modulo;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.mensaje.ConversacionResponse;
import com.sicad.sicad_backend.dto.mensaje.MensajeRequest;
import com.sicad.sicad_backend.dto.mensaje.MensajeResponse;
import com.sicad.sicad_backend.model.Mensaje;
import com.sicad.sicad_backend.model.Usuario;
import com.sicad.sicad_backend.repository.interfaces.IMensajeRepo;
import com.sicad.sicad_backend.repository.interfaces.IUsuarioRepo;
import com.sicad.sicad_backend.service.interfaces.IMensajeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MensajeServiceImpl implements IMensajeService {

    private final IMensajeRepo mensajeRepo;
    private final IUsuarioRepo usuarioRepo;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BaseObjectResponse<MensajeResponse> enviarMensaje(MensajeRequest request) {
        Optional<Usuario> remitenteOpt = usuarioRepo.findByIdAndEnabledTrue(request.getIdRemitente());
        Optional<Usuario> destinatarioOpt = usuarioRepo.findByIdAndEnabledTrue(request.getIdDestinatario());

        if (remitenteOpt.isEmpty() || destinatarioOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, "Usuario no encontrado", null);
        }

        Mensaje mensaje = Mensaje.builder()
                .remitente(remitenteOpt.get())
                .destinatario(destinatarioOpt.get())
                .contenido(request.getContenido())
                .build();

        Mensaje mensajeGuardado = mensajeRepo.save(mensaje);

        return new BaseObjectResponse<>(201, "Mensaje enviado exitosamente",
                convertirAMensajeResponse(mensajeGuardado));
    }

    @Override
    public BaseListReponse<MensajeResponse> obtenerConversacion(Integer idUsuario1, Integer idUsuario2) {
        List<Mensaje> mensajes = mensajeRepo.findConversacion(idUsuario1, idUsuario2);
        List<MensajeResponse> response = mensajes.stream()
                .map(this::convertirAMensajeResponse)
                .toList();

        return new BaseListReponse<>(200, "Conversación obtenida exitosamente", response);
    }

    @Override
    public BaseListReponse<ConversacionResponse> obtenerConversaciones(Integer idUsuario) {
        List<Integer> usuariosIds = mensajeRepo.findUsuariosConConversacion(idUsuario);
        List<ConversacionResponse> conversaciones = new ArrayList<>();

        for (Integer idOtroUsuario : usuariosIds) {
            Optional<Usuario> usuarioOpt = usuarioRepo.findByIdAndEnabledTrue(idOtroUsuario);
            if (usuarioOpt.isEmpty()) continue;

            Usuario otroUsuario = usuarioOpt.get();
            List<Mensaje> ultimoMensaje = mensajeRepo.findUltimoMensaje(idUsuario, idOtroUsuario);
            Integer noLeidos = mensajeRepo.contarMensajesNoLeidos(idOtroUsuario, idUsuario);

            if (!ultimoMensaje.isEmpty()) {
                Mensaje ultimo = ultimoMensaje.get(0);
                conversaciones.add(ConversacionResponse.builder()
                        .idUsuario(otroUsuario.getIdUsuario())
                        .nombreUsuario(otroUsuario.getNombre() + " " + otroUsuario.getApellido())
                        .ultimoMensaje(ultimo.getContenido())
                        .fechaUltimoMensaje(ultimo.getFechaEnvio())
                        .mensajesNoLeidos(noLeidos)
                        .build());
            }
        }

        conversaciones.sort((a, b) -> b.getFechaUltimoMensaje().compareTo(a.getFechaUltimoMensaje()));

        return new BaseListReponse<>(200, "Conversaciones obtenidas exitosamente", conversaciones);
    }

    @Override
    @Transactional
    public BaseObjectResponse<Void> marcarComoLeido(Integer idMensaje) {
        Optional<Mensaje> mensajeOpt = mensajeRepo.findById(idMensaje);
        if (mensajeOpt.isEmpty()) {
            return new BaseObjectResponse<>(404, "Mensaje no encontrado", null);
        }

        Mensaje mensaje = mensajeOpt.get();
        mensaje.setLeido(true);
        mensajeRepo.save(mensaje);

        return new BaseObjectResponse<>(200, "Mensaje marcado como leído", null);
    }

    @Override
    @Transactional
    public BaseObjectResponse<Void> marcarConversacionComoLeida(Integer idRemitente, Integer idDestinatario) {
        List<Mensaje> mensajes = mensajeRepo.findConversacion(idRemitente, idDestinatario);
        mensajes.stream()
                .filter(m -> m.getRemitente().getIdUsuario().equals(idRemitente) && !m.getLeido())
                .forEach(m -> {
                    m.setLeido(true);
                    mensajeRepo.save(m);
                });

        return new BaseObjectResponse<>(200, "Conversación marcada como leída", null);
    }

    private MensajeResponse convertirAMensajeResponse(Mensaje mensaje) {
        return MensajeResponse.builder()
                .idMensaje(mensaje.getIdMensaje())
                .idRemitente(mensaje.getRemitente().getIdUsuario())
                .nombreRemitente(mensaje.getRemitente().getNombre() + " " + mensaje.getRemitente().getApellido())
                .idDestinatario(mensaje.getDestinatario().getIdUsuario())
                .nombreDestinatario(mensaje.getDestinatario().getNombre() + " " + mensaje.getDestinatario().getApellido())
                .contenido(mensaje.getContenido())
                .fechaEnvio(mensaje.getFechaEnvio())
                .leido(mensaje.getLeido())
                .build();
    }
}