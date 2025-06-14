package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.presentation.dto.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private UsuarioDTO usuario;
}
