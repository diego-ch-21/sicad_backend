package com.sicad.sicad_backend.controller;

import com.sicad.sicad_backend.dto.base.BaseListReponse;
import com.sicad.sicad_backend.dto.base.BaseMessageResponse;
import com.sicad.sicad_backend.dto.base.BaseObjectResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaCreateRequest;
import com.sicad.sicad_backend.dto.escuela.EscuelaDetalleResponse;
import com.sicad.sicad_backend.dto.escuela.EscuelaUpdateRequest;
import com.sicad.sicad_backend.service.interfaces.IEscuelaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/escuela")
@RequiredArgsConstructor
@Tag(name = "Escuela", description = "Endpoints para la gestión de Escuelas")
public class EscuelaController {

    private final IEscuelaService service;

    // --- LISTAR ---
    @Operation(
            summary = "Listar escuelas",
            description = "Obtiene una lista de todas las escuelas registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de escuelas obtenida exitosamente")
    })
    @PreAuthorize("@access.isAdmin()")
    @GetMapping("/listar")
    public ResponseEntity<BaseListReponse<EscuelaDetalleResponse>>
    listar() throws Exception {
        BaseListReponse<EscuelaDetalleResponse> response = service.listar();
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- BUSCAR POR ID ---
    @Operation(
            summary = "Buscar escuela por ID",
            description = "Busca y obtiene los detalles de una escuela específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Escuela encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Escuela no encontrada",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PreAuthorize("@access.isAdmin()")
    @GetMapping("/buscar/{idEscuela}")
    public ResponseEntity<BaseObjectResponse<EscuelaDetalleResponse>>
    buscar(@PathVariable("idEscuela") Integer id) {
        BaseObjectResponse<EscuelaDetalleResponse> response = service.buscar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR (Individual) ---
    @Operation(
            summary = "Registrar escuela",
            description = "Crea y registra una nueva escuela en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Escuela registrada exitosamente")
    })
    @PreAuthorize("@access.isAdmin()")
    @PostMapping("/insertar")
    public ResponseEntity<BaseObjectResponse<EscuelaDetalleResponse>>
    registrar(@Valid @RequestBody EscuelaCreateRequest request) {
        BaseObjectResponse<EscuelaDetalleResponse> response = service.registrar(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- INSERTAR ALL (Múltiple) ---
    @Operation(
            summary = "Registrar múltiples escuelas",
            description = "Crea y registra múltiples escuelas en el sistema a partir de una lista."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Escuelas registradas exitosamente")
    })
    @PreAuthorize("@access.isAdmin()")
    @PostMapping("/insertar-all")
    public ResponseEntity<BaseListReponse<EscuelaDetalleResponse>>
    registrarAll(@Valid @RequestBody List<EscuelaCreateRequest> request) {
        BaseListReponse<EscuelaDetalleResponse> response = service.registrarAll(request);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ACTUALIZAR ---
    @Operation(
            summary = "Actualizar escuela",
            description = "Actualiza los datos de una escuela existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Escuela actualizada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Escuela no encontrada para actualizar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PreAuthorize("@access.isAdmin()")
    @PutMapping("/actualizar/{idEscuela}")
    public ResponseEntity<BaseObjectResponse<EscuelaDetalleResponse>>
    actualizar(@PathVariable("idEscuela") Integer id, @Valid @RequestBody EscuelaUpdateRequest dto) {
        BaseObjectResponse<EscuelaDetalleResponse> response = service.actualizar(id, dto);
        return ResponseEntity.status(response.status()).body(response);
    }

    // --- ELIMINAR ---
    @Operation(
            summary = "Eliminar escuela",
            description = "Elimina una escuela del sistema usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Escuela eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Escuela no encontrada para eliminar",
                    content = @Content(schema = @Schema(implementation = BaseMessageResponse.class)))
    })
    @PreAuthorize("@access.isAdmin()")
    @DeleteMapping("/eliminar/{idEscuela}")
    public ResponseEntity<BaseObjectResponse<String>>
    eliminar(@PathVariable("idEscuela") Integer id) {
        BaseObjectResponse<String> response = service.eliminar(id);
        return ResponseEntity.status(response.status()).body(response);
    }

}