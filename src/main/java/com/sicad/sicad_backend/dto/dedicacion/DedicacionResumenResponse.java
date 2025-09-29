package com.sicad.sicad_backend.dto.dedicacion;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DedicacionResumenResponse {
    private Integer idDedicacion;
    private String nombre;
    private Integer horasMaxLectivas;
}
