package com.sicad.sicad_backend.dto.facultad;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacultadCreateRequest {
    @NotBlank(message = "El nombre es un campo obligatorio")
    private String nombre;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}
