package com.sicad.sicad_backend.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BaseObjectResponse<T>(
        int status,
        String message,
        T data
) {
}
