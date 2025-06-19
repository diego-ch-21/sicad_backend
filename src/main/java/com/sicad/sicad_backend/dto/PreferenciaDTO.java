package com.sicad.sicad_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreferenciaDTO {
    private Integer idPreferencia;
    private Integer idDocente;
    private Integer idCurso;
    private Integer enabled;
}
