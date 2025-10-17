package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.asignatura.AsignaturaCreateRequest;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaDetalleResponse;
import com.sicad.sicad_backend.dto.asignatura.AsignaturaUpdateRequest;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse; // Importación necesaria
import com.sicad.sicad_backend.service.interfaces.IAsignaturaService;
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
@RequestMapping("/asignatura")
@RequiredArgsConstructor
@Tag(name = "Asignatura", description = "Endpoints para la gestión de Asignaturas")
public class AsignaturaController {

    private final IAsignaturaService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar asignaturas",
            description = "Obtiene una lista de todas las asignaturas registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de asignaturas obtenida exitosamente")
    })
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<AsignaturaDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<AsignaturaDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar asignatura por ID",
            description = "Busca y obtiene los detalles de una asignatura específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignatura encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @GetMapping("/buscar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>>
    buscar(@PathVariable("idAsignatura") Integer id) {
        BaseObjectResponse<AsignaturaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar asignatura",
            description = "Crea y registra una nueva asignatura en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asignatura registrada exitosamente")
    })
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>>
    registrar(@Valid @RequestBody AsignaturaCreateRequest request) {
        BaseObjectResponse<AsignaturaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples asignaturas",
            description = "Crea y registra múltiples asignaturas en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Asignaturas registradas exitosamente")
    })
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<AsignaturaDetalleResponse>>
    registrarAll(@Valid @RequestBody List<AsignaturaCreateRequest> request) {
        BaseListReponse<AsignaturaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar asignatura",
            description = "Actualiza los datos de una asignatura existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignatura actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PutMapping("/actualizar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<AsignaturaDetalleResponse>>
    actualizar(@PathVariable("idAsignatura") Integer id, @Valid @RequestBody AsignaturaUpdateRequest dto) {
        BaseObjectResponse<AsignaturaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar asignatura",
            description = "Elimina (lógicamente) una asignatura del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Asignatura eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Asignatura no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @DeleteMapping("/eliminar/{idAsignatura}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idAsignatura") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }
}