package com.sicad.sicad_backend.dto.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDetalleResponse {
    private Integer idUsuario;
    private Integer idRol;
    private String email;
    private boolean enabled;
    private String codigo;
    private String nombre;
    private String apellido;

}
