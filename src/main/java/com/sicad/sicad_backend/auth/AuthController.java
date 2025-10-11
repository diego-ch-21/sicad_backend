package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.auth.dto.AuthResponse;
import com.sicad.sicad_backend.auth.dto.LoginRequest;
import com.sicad.sicad_backend.auth.dto.RegisterRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class AuthController {
    private final IAuthService service;
    @Operation(
            summary = "login",
            description = "Valida las credenciales de un usuario y devuelve un token JWT junto con la información básica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("login")
    public ResponseEntity<BaseObjectResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        BaseObjectResponse<AuthResponse> response = service.login(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping(value = "register/admin")
    public ResponseEntity<BaseObjectResponse<AuthResponse>> registerUserAdmin(@Valid @RequestBody RegisterRequest request) {
        BaseObjectResponse<AuthResponse> response = service.registerAdmin(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    /*
    @PostMapping(value = "register/director")
    public ResponseEntity<GenericObjectResponse<AuthResponse>> registerUserDirector(@RequestBody RegisterRequest request) {
        GenericObjectResponse<AuthResponse> response = authService.registerAdmin(request,2);
        return ResponseEntity.status(response.status()).body(response);
    }
    @PostMapping(value = "register/profesor")
    public ResponseEntity<GenericObjectResponse<AuthResponse>> registerUserProfesor(@RequestBody RegisterRequest request) {
        GenericObjectResponse<AuthResponse> response = authService.registerAdmin(request,3);
        return ResponseEntity.status(response.status()).body(response);
    }

     */

}
