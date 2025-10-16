package com.sicad.sicad_backend.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sicad.sicad_backend.dto.usuario.UsuarioDetalleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String token;
    private UsuarioDetalleResponse usuario;
    private RolesResponse roles;
}
