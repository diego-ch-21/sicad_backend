package com.sicad.sicad_backend.dto.preferencia;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaCreateRequest {
    @NotBlank(message = "El idDocente es un campo obligatorio")
    private Integer idDocente;
    @NotBlank(message = "El idCurso es un campo obligatorio")
    private Integer idCurso;
    @NotBlank(message = "El idCargaElectiva es un campo obligatorio")
    private Integer idCargaElectiva;
}
