package com.sicad.sicad_backend.dto.docente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sicad.sicad_backend.dto.categoria.CategoriaRequestDTO;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteRequestDTO {
    private String email;
    private String password;
    private String nombres;
    private String apellidos;
    private Integer idDedicacion;
    private Integer idCategoria;
    private Integer horasMaxLectivas;
    private Boolean tienePermisoExceso;
}
