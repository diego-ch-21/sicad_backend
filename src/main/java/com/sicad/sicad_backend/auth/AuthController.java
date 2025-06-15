package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.presentation.dto.base.GenericObjectResponse;
import com.sicad.sicad_backend.presentation.dto.base.GenericReponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
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
    public ResponseEntity<GenericObjectResponse<AuthResponse>> login(@RequestBody LoginRequest request) {
        GenericObjectResponse<AuthResponse> response = authService.login(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    @PostMapping(value = "register/admin")
    public ResponseEntity<GenericObjectResponse<AuthResponse>> registerUserAdmin(@RequestBody RegisterRequest request) {
        GenericObjectResponse<AuthResponse> response = authService.registerAdmin(request,1);
        return ResponseEntity.status(response.status()).body(response);
    }
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

}
