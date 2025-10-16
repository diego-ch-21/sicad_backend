package com.sicad.sicad_backend.dto.departamentoAcademico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartamentoAcademicoUpdateRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;
}
