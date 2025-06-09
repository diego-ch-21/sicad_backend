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

    //@NonNull
    //@JsonIncludeProperties(value = {"idRol"})
    //private RolDTO rol;
    private Integer idRol;
    @JsonProperty(value = "user_name")
    @NonNull
    private String username;
    @NonNull
    @Size(min = 5, max=60)
    //@JsonIgnore
    // solo escritura
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    // solo lectura
    //@JsonProperty(access= JsonProperty.Access.READ_ONLY)
    private String password; // Bcrypt
    private boolean enabled;
    private String nombres;
    private String apellidos;
    private String email;
}
