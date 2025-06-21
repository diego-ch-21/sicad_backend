package com.sicad.sicad_backend.dto.docente;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class insertarDocenteRequest {
    private String email;
    private String password;
    private String codigo;
    private String nombres;
    private String apellidos;
    private Integer idDedicacion;
    private Integer idCategoria;
    private Integer horasMaxLectivas;
    private Boolean tienePermisoExceso;
    private Date fechaCreacion;

}
