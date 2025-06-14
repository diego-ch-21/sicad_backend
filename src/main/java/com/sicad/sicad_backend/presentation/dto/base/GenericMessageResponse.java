package com.sicad.sicad_backend.presentation.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenericMessageResponse(
        int status,
        String message
) {
}
