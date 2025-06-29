package com.sicad.sicad_backend.dto.escuela;

import com.sicad.sicad_backend.dto.facultad.FacultadResumenResponse;
import com.sicad.sicad_backend.model.Facultad;
import jakarta.persistence.Column;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EscuelaDetalleResponse {
    private Integer idEscuela;
    private String codigo;
    private String nombre;
    private FacultadResumenResponse facultad;
    private boolean enabled;
}
