package com.sicad.sicad_backend.dto.docente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.categoria.CategoriaRequestDTO;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionRequestDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.sicad.sicad_backend.utils.MensajesValidacion.campoRequerido;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteRequestDTO {
    @NotBlank(message = "El email es un campo obligatorio")
    private String email;
    @NotBlank(message = "El password es un campo obligatorio")
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
    private Boolean tienePermisoExceso=false;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private boolean enabled=true;
}
