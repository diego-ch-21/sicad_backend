package com.sicad.sicad_backend.dto.mensaje;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConversacionResponse {
    private Integer idUsuario;
    private String nombreUsuario;
    private String ultimoMensaje;
    private LocalDateTime fechaUltimoMensaje;
    private Integer mensajesNoLeidos;
}