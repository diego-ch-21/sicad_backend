package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.dedicacion.DedicacionCreateRequest;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionDetalleResponse;
import com.sicad.sicad_backend.dto.dedicacion.DedicacionUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IDedicacionService;
import io.swagger.v3.oas.annotations.Operation; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Content; // Importación necesaria
import io.swagger.v3.oas.annotations.media.Schema; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponse; // Importación necesaria
import io.swagger.v3.oas.annotations.responses.ApiResponses; // Importación necesaria
import io.swagger.v3.oas.annotations.tags.Tag; // Importación necesaria
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/dedicacion")
@RequiredArgsConstructor
@Tag(name = "Dedicación", description = "Endpoints para la gestión de Dedicaciones Docentes")
public class DedicacionController {

    private final IDedicacionService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar dedicaciones",
            description = "Obtiene una lista de todas las dedicaciones (tiempos de contrato) registradas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de dedicaciones obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<DedicacionDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<DedicacionDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar dedicación por ID",
            description = "Busca y obtiene los detalles de una dedicación específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dedicación encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Dedicación no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idDedicacion}")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>>
    buscar(@PathVariable("idDedicacion") Integer id) {
        BaseObjectResponse<DedicacionDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR DOCENTE ---
    @Operation(
            summary = "Buscar dedicación por ID de Docente",
            description = "Busca y obtiene la dedicación actual asociada al ID del docente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dedicación encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Dedicación o Docente no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar-por-docente/{idDocente}")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>>
    buscarPorDocente(@PathVariable("idDocente") Integer id) {
        BaseObjectResponse<DedicacionDetalleResponse> response = service.buscarPorDocente(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar dedicación",
            description = "Crea y registra una nueva dedicación en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dedicación registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>>
    registrar(@Valid @RequestBody DedicacionCreateRequest request) {
        BaseObjectResponse<DedicacionDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples dedicaciones",
            description = "Crea y registra múltiples dedicaciones en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dedicaciones registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<DedicacionDetalleResponse>>
    registrarAll(@Valid @RequestBody List<DedicacionCreateRequest> request) {
        BaseListReponse<DedicacionDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar dedicación",
            description = "Actualiza los datos de una dedicación existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dedicación actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Dedicación no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idDedicacion}")
    public ResponseEntity<BaseObjectResponse<DedicacionDetalleResponse>>
    actualizar(@PathVariable("idDedicacion") Integer id,
               @Valid @RequestBody DedicacionUpdateRequest request) {
        BaseObjectResponse<DedicacionDetalleResponse> response = service.actualizar(id, request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar dedicación",
            description = "Elimina (lógicamente) una dedicación del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dedicación eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Dedicación no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idDedicacion}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idDedicacion") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}