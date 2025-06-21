package com.sicad.sicad_backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "El email es un campo obligatorio")
    @Email(message = "El email debe debe tener el formato correcto")
    String email;
    @NotBlank(message = "El password es un campo obligatorio")
    String password;
}
