package com.sicad.sicad_backend.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseListPageResponse<T>(
        int status,
        String message,
        List<T> data,
        int numeroPagina,
        int totalPaginas,
        long totalElementos
) {
}
