package com.sicad.sicad_backend.controller;


import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaCreateRequest;
import com.sicad.sicad_backend.dto.logistica.LogisticaDetalleResponse;
import com.sicad.sicad_backend.dto.logistica.LogisticaUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.ILogisticaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logistica")
@RequiredArgsConstructor
@Tag(name = "Logística", description = "Endpoints para la gestión de Logística")
public class LogisticaController {
    private final ILogisticaService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar logísticas",
            description = "Obtiene una lista de todas las logísticas registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de logísticas obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<LogisticaDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<LogisticaDetalleResponse> response = service.listar();
        // Ajustado para usar el status de la respuesta para consistencia
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar logística por ID",
            description = "Busca y obtiene los detalles de una logística específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logística encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Logística no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idLogistica}")
    public ResponseEntity<BaseObjectResponse<LogisticaDetalleResponse>>
    buscar(@PathVariable("idLogistica") Integer id) {
        BaseObjectResponse<LogisticaDetalleResponse> response = service.buscar(id);
        // Ajustado para usar el status de la respuesta para consistencia
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar logística",
            description = "Crea y registra una nueva logística en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Logística registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<LogisticaDetalleResponse>>
    registrar(@Valid @RequestBody LogisticaCreateRequest request) {
        BaseObjectResponse<LogisticaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples logísticas",
            description = "Crea y registra múltiples logísticas en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Logísticas registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<LogisticaDetalleResponse>>
    registrarAll(@Valid @RequestBody List<LogisticaCreateRequest> request) {
        BaseListReponse<LogisticaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar logística",
            description = "Actualiza los datos de una logística existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logística actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Logística no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idLogistica}")
    public ResponseEntity<BaseObjectResponse<LogisticaDetalleResponse>>
    actualizar(@PathVariable("idLogistica") Integer id, @Valid @RequestBody LogisticaUpdateRequest dto) {
        BaseObjectResponse<LogisticaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar logística",
            description = "Elimina (lógicamente) una logística del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logística eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Logística no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idLogistica}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idLogistica") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}