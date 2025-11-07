package com.sicad.sicad_backend.dto.mensaje;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MensajeRequest {
    @NotNull(message = "El ID del remitente es obligatorio")
    private Integer idRemitente;

    @NotNull(message = "El ID del destinatario es obligatorio")
    private Integer idDestinatario;

    @NotBlank(message = "El contenido del mensaje no puede estar vacío")
    private String contenido;
}