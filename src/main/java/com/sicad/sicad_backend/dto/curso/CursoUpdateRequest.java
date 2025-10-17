package com.sicad.sicad_backend.dto.curso;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CursoUpdateRequest {
    private Integer idAsignatura;
    private List<String> planDeEstudios = new ArrayList<>();
    private Integer idEscuela;
    private Integer idCicloAcademico;
    @Min(value = 1, message = "El ciclo debe ser mínimo 1")
    @Max(value = 10, message = "El ciclo debe ser máximo 10")
    private Integer ciclo;
    //opcional
    @Pattern(regexp = "^G[1-9][0-9]*$", message = "El grupo debe tener el formato G1, G2, G3, etc.")
    private String grupo;
}
