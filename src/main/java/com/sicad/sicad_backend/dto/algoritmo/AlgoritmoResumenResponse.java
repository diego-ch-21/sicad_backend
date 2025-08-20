package com.sicad.sicad_backend.dto.algoritmo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor  // 👈 Necesario para ModelMapper
@AllArgsConstructor // 👈 Constructor con todos los campos
public class AlgoritmoResumenResponse {

    private Integer idAlgoritmo;
    private boolean principal;
    private LocalDateTime createdAt;
    private boolean enabled;
}
