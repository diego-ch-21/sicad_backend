package com.sicad.sicad_backend.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

public class insertarDocenteRequest {
    @JsonProperty(value = "user_name")
    @NonNull
    private String username;
    @NonNull
    @Size(min = 6)
    private String password;
    private boolean enabled;
    private String codigo;
    private String nombres;
    private String apellidos;
    private String email;
    private Integer idDocente;
    private Integer idUsuario;
    private String categoria;
    private Integer horasMaxLectivas;
    private boolean isPermiso;
}
