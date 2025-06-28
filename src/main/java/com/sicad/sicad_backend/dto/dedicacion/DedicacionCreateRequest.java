package com.sicad.sicad_backend.dto.dedicacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DedicacionCreateRequest {
    @NotBlank(message = "nombre es un campo obligatorio")
    private String nombre;
    @NotNull(message = "horasTotales es un campo obligatorio")
    private Integer horasTotales;
    @NotNull(message = "horasLectivasMinima es un campo obligatorio")
    private Integer horasLectivasMinima;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean enabled=true;
}
