package com.sicad.sicad_backend.dto.cicloAcademico;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CicloAcademicoFileResponse {
    private Integer idCicloAcademico;
    private String urlPdf;
    private String urlExcel;
    private Boolean enabled;
}
