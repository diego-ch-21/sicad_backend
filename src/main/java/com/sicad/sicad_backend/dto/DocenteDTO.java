package com.sicad.sicad_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteDTO {
    private Integer idDocente;
    private Integer idUsuario;
    private Integer idDedicacion;
    private Integer idCategoria;
    private Integer horasMaxLectivas;
    private Boolean tienePermisoExceso;
    private ZonedDateTime fechaCreacion;
    private boolean enabled;
}
