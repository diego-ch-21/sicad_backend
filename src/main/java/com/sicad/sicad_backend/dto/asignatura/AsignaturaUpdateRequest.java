package com.sicad.sicad_backend.dto.asignatura;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsignaturaUpdateRequest {
    private String nombre;
}
