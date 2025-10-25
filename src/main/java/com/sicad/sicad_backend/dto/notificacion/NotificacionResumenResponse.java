package com.sicad.sicad_backend.dto.notificacion;

import com.sicad.sicad_backend.Enum.TipoNotificacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionResumenResponse {
    private Integer idNotificacion;

    private String mensaje;

    private LocalDateTime createdAt;

    private Boolean leida;

    private TipoNotificacion tipo;

}
