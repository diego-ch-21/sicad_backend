package com.sicad.sicad_backend.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    @NonNull
    private Integer idUsuario;
    private String idRol;
    @JsonProperty(value = "user_name")
    @NonNull
    private String username;
    @NonNull
    @Size(min = 5, max=60)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean enabled;
    private String codigo;
    private String nombres;
    private String apellidos;
    private String email;
}
