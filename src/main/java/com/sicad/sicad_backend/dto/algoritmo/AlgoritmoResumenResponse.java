package com.sicad.sicad_backend.dto.algoritmo;

import java.time.LocalDateTime;

public record AlgoritmoResumenResponse(
        Integer idAlgoritmo,
        LocalDateTime createdAt,
        boolean enabled
) {
}
