package com.sicad.sicad_backend.dto.base;

import java.time.LocalDateTime;

public record CustomErrorResponse(
        LocalDateTime datatime,
        String message,
        String path
) {

}
