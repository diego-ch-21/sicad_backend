package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioCreateRequest;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioDetalleResponse;
import com.sicad.sicad_backend.dto.planDeEstudio.PlanDeEstudioUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IPlanDeEstudioService;
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
@RequestMapping("/plan-de-estudio")
@RequiredArgsConstructor
@Tag(name = "Plan de Estudio", description = "Endpoints para la gestión de Planes de Estudio")
public class PlanDeEstudioController {

    private final IPlanDeEstudioService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar planes de estudio",
            description = "Obtiene una lista de todos los planes de estudio registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de planes de estudio obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<PlanDeEstudioDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<PlanDeEstudioDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar plan de estudio por ID",
            description = "Busca y obtiene los detalles de un plan de estudio específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan de estudio encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Plan de estudio no encontrado",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idPlanDeEstudio}")
    public ResponseEntity<BaseObjectResponse<PlanDeEstudioDetalleResponse>>
    buscar(@PathVariable("idPlanDeEstudio") Integer id) {
        BaseObjectResponse<PlanDeEstudioDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar plan de estudio",
            description = "Crea y registra un nuevo plan de estudio en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plan de estudio registrado exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<PlanDeEstudioDetalleResponse>>
    registrar(@Valid @RequestBody PlanDeEstudioCreateRequest request) {
        BaseObjectResponse<PlanDeEstudioDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples planes de estudio",
            description = "Crea y registra múltiples planes de estudio en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Planes de estudio registrados exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<PlanDeEstudioDetalleResponse>>
    registrarAll(@Valid @RequestBody List<PlanDeEstudioCreateRequest> request) {
        BaseListReponse<PlanDeEstudioDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar plan de estudio",
            description = "Actualiza los datos de un plan de estudio existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan de estudio actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Plan de estudio no encontrado para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idPlanDeEstudio}")
    public ResponseEntity<BaseObjectResponse<PlanDeEstudioDetalleResponse>>
    actualizar(@PathVariable("idPlanDeEstudio") Integer id, @Valid @RequestBody PlanDeEstudioUpdateRequest dto) {
        BaseObjectResponse<PlanDeEstudioDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar plan de estudio",
            description = "Elimina (lógicamente) un plan de estudio del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan de estudio eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Plan de estudio no encontrado para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idPlanDeEstudio}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idPlanDeEstudio") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}