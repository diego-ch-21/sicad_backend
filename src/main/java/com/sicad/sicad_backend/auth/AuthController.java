package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.auth.dto.AuthResponse;
import com.sicad.sicad_backend.auth.dto.LoginRequest;
import com.sicad.sicad_backend.auth.dto.RegisterRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints para autenticación")
public class AuthController {

    private final IAuthService service;

    @Operation(
            summary = "Login",
            description = "Valida las credenciales de un usuario y devuelve un token JWT junto con la información básica del usuario."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa")
    })
    @PostMapping("/login")
    public ResponseEntity<BaseObjectResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        BaseObjectResponse<AuthResponse> response = service.login(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @Operation(
            summary = "Registrar administrador",
            description = "Registra un nuevo usuario con rol de administrador en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario administrador registrado exitosamente")
    })
    @PostMapping("/register/admin")
    public ResponseEntity<BaseObjectResponse<AuthResponse>> registerUserAdmin(
            @Valid @RequestBody RegisterRequest request) {
        BaseObjectResponse<AuthResponse> response = service.registerAdmin(request);
        return ResponseEntity.status(response.status()).body(response);
    }

}
