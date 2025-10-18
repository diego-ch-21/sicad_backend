package com.sicad.sicad_backend.dto.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.rol.RolDetalleResponse;
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
    private RolDetalleResponse rol;
    private String email;
    private boolean enabled;
    private String codigo;
    private String nombre;
    private String apellido;

}
