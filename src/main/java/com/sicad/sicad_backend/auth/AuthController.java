package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.auth.dto.AuthResponse;
import com.sicad.sicad_backend.auth.dto.LoginRequest;
import com.sicad.sicad_backend.auth.dto.RegisterRequest;
import com.sicad.sicad_backend.dto.base.GenericObjectResponse;
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
    private final AuthService authService;

    @PostMapping("login")
    public ResponseEntity<GenericObjectResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        GenericObjectResponse<AuthResponse> response = authService.login(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping(value = "register/admin")
    public ResponseEntity<GenericObjectResponse<AuthResponse>> registerUserAdmin(@Valid @RequestBody RegisterRequest request) {
        GenericObjectResponse<AuthResponse> response = authService.registerAdmin(request);
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
