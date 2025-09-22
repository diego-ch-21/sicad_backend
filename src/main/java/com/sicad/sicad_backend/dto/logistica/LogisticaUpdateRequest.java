package com.sicad.sicad_backend.dto.logistica;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticaUpdateRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private String cargo;
}
