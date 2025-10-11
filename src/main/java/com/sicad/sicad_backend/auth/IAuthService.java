package com.sicad.sicad_backend.auth;

import com.sicad.sicad_backend.auth.dto.AuthResponse;
import com.sicad.sicad_backend.auth.dto.LoginRequest;
import com.sicad.sicad_backend.auth.dto.RegisterRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;

public interface IAuthService {
    BaseObjectResponse<AuthResponse> login(LoginRequest request);
    BaseObjectResponse<AuthResponse> registerAdmin(RegisterRequest request);
}
