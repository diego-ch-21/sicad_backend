package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionConteoResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionDetalleResponse;
import com.sicad.sicad_backend.dto.notificacion.NotificacionResumenResponse;
import com.sicad.sicad_backend.service.interfaces.INoficacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notificacion")
@RequiredArgsConstructor
@Tag(name = "Notificación", description = "Endpoints para la gestión de notificaciones del sistema")
public class NotificacionController {

    private final INoficacionService service;

    // --- LISTAR NOTIFICACIONES POR USUARIO ---
    @Operation(summary = "Listar notificaciones de un usuario", description = "Obtiene todas las notificaciones de un usuario, ordenadas por fecha descendente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificaciones obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron notificaciones para el usuario",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<BaseListReponse<NotificacionResumenResponse>> listarPorUsuario(
            @PathVariable("idUsuario") Integer idUsuario) {

        BaseListReponse<NotificacionResumenResponse> response = service.listarNotificacionesPorUsuario(idUsuario);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- MARCAR COMO LEÍDA ---
    @Operation(summary = "Marcar una notificación como leída", description = "Cambia el estado de una notificación a 'leída'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación marcada como leída correctamente"),
            @ApiResponse(responseCode = "404", description = "Notificación no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/{idNotificacion}/leida")
    public ResponseEntity<BaseObjectResponse<String>> marcarComoLeida(
            @PathVariable("idNotificacion") Integer idNotificacion) {

        BaseObjectResponse<String> response = service.marcarNotificacionComoLeida(idNotificacion);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- CONTEO DE NOTIFICACIONES ---
    @Operation(summary = "Contar notificaciones por estado", description = "Devuelve la cantidad de notificaciones leídas, no leídas y el total para un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conteo obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron notificaciones para el usuario",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/usuario/{idUsuario}/conteo")
    public ResponseEntity<BaseObjectResponse<NotificacionConteoResponse>> contarPorUsuario(
            @PathVariable("idUsuario") Integer idUsuario) {

        BaseObjectResponse<NotificacionConteoResponse> response = service.contarNotificacionesPorEstado(idUsuario);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- NOTIFICAR DOCENTES NUEVO CICLO ---
    @Operation(summary = "Notificar a docentes sobre nuevo ciclo académico",
            description = "Envía una notificación a todos los docentes cuando se crea un nuevo ciclo académico.")
    @PostMapping("/notificar-docentes-ciclo/{idCiclo}")
    public ResponseEntity<BaseObjectResponse<String>> notificarDocentesNuevoCiclo(
            @PathVariable("idCiclo") Integer idCiclo) {

        BaseObjectResponse<String> response = service.notificarADocentesNuevoCicloAcademico(idCiclo);
        return ResponseEntity.status(response.status()).body(response);
    }


    // Notificar a departamentos académicos sobre preferencias y disponibilidad completadas
    @Operation(
            summary = "Notificar a departamentos académicos sobre docente que completó preferencias",
            description = "Envía una notificación a todos los departamentos académicos informando que un docente ha completado su registro de preferencias y disponibilidad."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación enviada correctamente"),
            @ApiResponse(responseCode = "404", description = "Docente o departamentos no encontrados",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PostMapping("/departamentos/pref-disponibilidad/{idDocente}")
    public ResponseEntity<BaseObjectResponse<String>> notificarADepartamentosPorPrefDisCompleto(
            @PathVariable Integer idDocente) {

        BaseObjectResponse<String> response =
                service.notificarADepartamentoAcademicoSobrePrefDisCompleto(idDocente);

        return ResponseEntity.status(response.status()).body(response);
    }

    // Notificar a docente sobre aprobación de preferencias por departamento académico
    @Operation(
            summary = "Notificar a docente sobre aprobación de preferencias",
            description = "Envía una notificación a un docente indicando que su registro de preferencias y disponibilidad ha sido aprobado por un departamento académico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificación enviada correctamente"),
            @ApiResponse(responseCode = "404", description = "Docente o departamento académico no encontrados",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PostMapping("/docente/aprobacion-pref-dis/{idDocente}")
    public ResponseEntity<BaseObjectResponse<String>> notificarADocenteSobreAprobacionPrefDis(
            @PathVariable Integer idDocente) {

        BaseObjectResponse<String> response =
                service.notificarADocenteSobreAprobacionPrefDis(idDocente);

        return ResponseEntity.status(response.status()).body(response);
    }

}
