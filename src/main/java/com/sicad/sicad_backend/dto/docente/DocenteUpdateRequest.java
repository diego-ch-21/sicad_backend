package com.sicad.sicad_backend.dto.docente;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteUpdateRequest {
    @NotBlank(message = "El email es un campo obligatorio")
    private String email;
    private String password;
    @NotBlank(message = "Los nombre es un campo obligatorio")
    private String nombre;
    @NotBlank(message = "Los apellido es un campo obligatorio")
    private String apellido;
    @NotNull(message = "El idDedicacion es un campo obligatorio")
    private Integer idDedicacion;
    @NotNull(message = "El idCategoria es un campo obligatorio")
    private Integer idCategoria;
    private Integer horasMaxLectivas;
}
