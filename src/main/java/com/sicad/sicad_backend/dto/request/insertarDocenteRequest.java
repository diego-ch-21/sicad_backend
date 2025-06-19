package com.sicad.sicad_backend.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.NonNull;

import java.util.Date;

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
