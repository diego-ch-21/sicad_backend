package com.sicad.sicad_backend.dto.notificacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionConteoResponse {
    private Integer leidas;
    private Integer noLeidas;
    private Integer total;
}
