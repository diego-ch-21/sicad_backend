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
public class DedicacionDetalleResponse {
    private Integer idDedicacion;
    private String nombre;
    private Integer horasTotales;
    private Integer horasMinLectivas;
    private Integer horasMaxLectivas;
    private Boolean enabled;
}
