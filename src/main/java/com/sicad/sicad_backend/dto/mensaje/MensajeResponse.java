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
public class MensajeResponse {
    private Integer idMensaje;
    private Integer idRemitente;
    private String nombreRemitente;
    private Integer idDestinatario;
    private String nombreDestinatario;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
}