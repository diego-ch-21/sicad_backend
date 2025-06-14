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

    @PostMapping(value = "register")
    public ResponseEntity<GenericObjectResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        GenericObjectResponse<AuthResponse> response = authService.register(request);
        return ResponseEntity.status(response.status()).body(response);
    }

}
