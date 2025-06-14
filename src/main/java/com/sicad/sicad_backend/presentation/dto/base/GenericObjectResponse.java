package com.sicad.sicad_backend.presentation.dto.base;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenericObjectResponse<T>(
        int status,
        String message,
        T data
) {
}
