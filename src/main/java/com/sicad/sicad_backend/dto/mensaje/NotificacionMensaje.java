package com.sicad.sicad_backend.dto.mensaje;

import com.sicad.sicad_backend.Enum.TipoMensaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacionMensaje {
    private TipoMensaje tipo; // "MENSAJE", "ESCRIBIENDO", "LEIDO"
    private MensajeResponse mensaje;
    private Integer idUsuario;
}